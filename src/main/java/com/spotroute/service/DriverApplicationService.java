package com.spotroute.service;

import com.spotroute.core.enums.ApplicationStatus;
import com.spotroute.core.enums.DocumentStatus;
import com.spotroute.core.enums.DocumentType;
import com.spotroute.core.enums.DriverStatus;
import com.spotroute.core.exceptions.CustomException;
import com.spotroute.dto.request.DriverApplyRequest;
import com.spotroute.dto.response.ApplicationResponse;
import com.spotroute.dto.response.DocumentResponse;
import com.spotroute.persistence.entity.DriverDocument;
import com.spotroute.persistence.entity.DriverProfile;
import com.spotroute.persistence.entity.User;
import com.spotroute.repository.DriverProfileRepository;
import com.spotroute.util.SecurityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Driver;
import java.time.Instant;
import java.util.*;

import static com.spotroute.core.enums.DocumentType.*;

@Service
@RequiredArgsConstructor
public class DriverApplicationService {

    private final DriverProfileRepository driverProfileRepository;
    private final FileService fileService;


    @Transactional
    public ApplicationResponse apply(DriverApplyRequest req) {
        User user = SecurityUtil.getLoggedInUserFromContext();
        if (user == null) {
            throw new IllegalStateException("No authenticated user in security context");
        }
        if (driverProfileRepository.existsByUserId(user.getId())) {
            throw new CustomException("User already has a driver application", HttpStatus.BAD_REQUEST);
        }

        Map<DocumentType, MultipartFile> files = new EnumMap<>(DocumentType.class);
        files.put(DRIVERS_LICENSE_SCAN, req.getDriversLicenseScan());
        files.put(PROOF_OF_OWNERSHIP, req.getProofOfOwnership());
        files.put(INSURANCE_CERTIFICATE, req.getInsuranceCertificate());
        files.put(VEHICLE_CONDITION_VIDEO, req.getVehicleConditionVideo());

        // validate everything before writing anything to disk
        for (DocumentType type : REQUIRED_DOCUMENT_TYPES) {
            if (!files.containsKey(type)) {
                throw new IllegalStateException("apply() has no file mapping for " + type);
            }
            fileService.validateDriverDocument(type, files.get(type));
        }

        DriverProfile profile = new DriverProfile();
        profile.setUser(user);
        profile.setApplicationStatus(ApplicationStatus.PENDING);
        profile.setLicenseNumber(req.getLicenseNumber().trim());
        profile.setVehicleModel(req.getVehicleModel().trim());
        profile.setVehicleColor(req.getVehicleColor().trim());
        profile.setVehiclePlate(req.getVehiclePlate().trim());
        profile.setGovernmentId(req.getGovernmentId().trim());
        profile.setLasdriCardNumber(req.getLasdriCardNumber().trim());

        List<String> storedPaths = new ArrayList<>();
        try {
            for (DocumentType type : REQUIRED_DOCUMENT_TYPES) {
                String path = fileService.storeDriverDocument(profile.getId(), type, files.get(type));
                storedPaths.add(path);
                DriverDocument doc = new DriverDocument();
                doc.setType(type);
                doc.setFileUrl(path);
                doc.setStatus(DocumentStatus.PENDING);
                profile.addDocument(doc);
            }
            driverProfileRepository.saveAndFlush(profile);
        } catch (DataIntegrityViolationException e) {
            // concurrent apply hit the unique constraint on user_id
            storedPaths.forEach(fileService::deleteQuietly);
            if (isDuplicateUserConstraint(e)) {
                throw new CustomException("You already have a driver application", HttpStatus.CONFLICT);
            }
            throw e;
        } catch (RuntimeException e) {
            storedPaths.forEach(fileService::deleteQuietly);
            throw e;
        }
        return toResponse(profile);
    }

//    @Transactional(readOnly = true)
//    public ApplicationResponse getApplication() {
//        DriverProfile driverProfile = currentActiveDriver();
//        return toResponse());
//    }


    @Transactional
    public ApplicationResponse reuploadDocument(DocumentType type, MultipartFile file) {
        DriverProfile driverProfile = currentActiveDriver();
        driverProfileRepository.findByUserIdForUpdate(Long.valueOf(driverProfile.getId()))
                .orElseThrow(() -> new CustomException("No driver application found", HttpStatus.NOT_FOUND));
        DriverDocument doc = driverProfile.findDocument(type)
                .orElseThrow(() -> new IllegalStateException(
                        "Profile " + driverProfile.getId() + " is missing required document " + type));

        if (doc.getStatus() != DocumentStatus.REJECTED) {
            throw new CustomException("Only rejected documents can be re-uploaded (" + type
                    + " is " + doc.getStatus() + ")", HttpStatus.BAD_REQUEST);
        }
        fileService.validateDriverDocument(type, file);

        String oldPath = doc.getFileUrl();
        String newPath = fileService.storeDriverDocument(driverProfile.getId(), type, file);
        try {
            doc.setFileUrl(newPath);
            doc.setStatus(DocumentStatus.PENDING);
            doc.setRejectionReason(null);
            doc.setReviewedBy(null);
            doc.setReviewedAt(null);
            doc.setUploadedAt(Instant.now());
            driverProfile.recalculateProfileStatus();
            driverProfileRepository.saveAndFlush(driverProfile);
        } catch (RuntimeException e) {
            fileService.deleteQuietly(newPath);
            throw e;
        }
        fileService.deleteQuietly(oldPath);
        return toResponse(driverProfile);
    }


    /** Call at token issue AND at token refresh, reading from the DB, never from the old token. */
    @Transactional(readOnly = true)
    public List<String> resolveAuthorities(Long userId) {
        boolean approved = driverProfileRepository.findStatusByUserId(userId)
                .map(s -> s == ApplicationStatus.APPROVED)
                .orElse(false); // no profile simply means "not a driver"
        return approved ? List.of("ROLE_USER", "ROLE_DRIVER") : List.of("ROLE_USER");
    }


    private DriverProfile currentActiveDriver() {
        DriverProfile driver = SecurityUtil.getLoggedInDriver();
        if (driver.getStatus() != DriverStatus.ACTIVE) {
            throw new CustomException("Driver account is deactivated", HttpStatus.FORBIDDEN);
        }
        return driver;
    }

    static ApplicationResponse toResponse(DriverProfile p) {
        List<DocumentResponse> docs = p.getDocuments().stream()
                .sorted(Comparator.comparing(DriverDocument::getType))
                .map(d -> new DocumentResponse(d.getType(), d.getStatus(), d.getRejectionReason(),
                        d.getUploadedAt(), d.getReviewedAt()))
                .toList();
        return new ApplicationResponse(
                p.getApplicationStatus(),
                p.getCreatedAt(),
                p.getApprovedAt(),
                docs);
    }

    private boolean isDuplicateUserConstraint(DataIntegrityViolationException e) {
        String cause = e.getMostSpecificCause().getMessage();
        return cause != null && cause.contains("driver_profiles_user_id_key");
    }


}