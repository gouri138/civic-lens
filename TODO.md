# Implement Geolocation and Image Uploads for Complaints

## Backend Changes
- [x] Update Complaint entity: Add latitude, longitude, region, imageUrl, proofImageUrl fields
- [x] Update ComplaintDTO: Add corresponding fields
- [x] Update ComplaintService: Modify submitComplaint to handle new fields and file uploads
- [x] Update ComplaintController: Change submitComplaint to multipart, add proof upload endpoint
- [x] Update SecurityConfig: Allow access to /uploads/**

## Frontend Changes
- [x] Modify complaints.html: Add geolocation capture, file input for image, change to FormData submission, display proof images
- [x] Update department-dashboard.html: Add proof image upload for status updates

## Configuration and Testing
- [x] Create uploads/ directory in backend
- [x] Test file uploads and geolocation
- [x] Verify static resource serving
- [x] Run application and test end-to-end
