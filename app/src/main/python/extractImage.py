import cv2
import numpy as np
import imutils

def extract_license_plate(image_bytes):
    # Convert byte array to NumPy array
    np_arr = np.frombuffer(image_bytes, np.uint8)
    image_array = cv2.imdecode(np_arr, cv2.IMREAD_COLOR)

    # Convert the image to grayscale
    gray = cv2.cvtColor(image_array, cv2.COLOR_BGR2GRAY)

    # Apply bilateral filter to remove noise while keeping edges sharp
    filtered = cv2.bilateralFilter(gray, 11, 17, 17)

    # Perform edge detection
    edged = cv2.Canny(filtered, 30, 200)

    # Find contours
    ext_contours = cv2.findContours(edged.copy(), cv2.RETR_TREE, cv2.CHAIN_APPROX_SIMPLE)
    contours = imutils.grab_contours(ext_contours)
    contours = sorted(contours, key=cv2.contourArea, reverse=True)[:10]

    # Initialize the license plate contour location
    location = None

    # Loop over contours to find a quadrilateral
    for contour in contours:
        approx = cv2.approxPolyDP(contour, 10, True)
        if len(approx) == 4:
            location = approx
            break

    if location is None:
        return image_bytes

    # Create a mask for the license plate
    mask = np.zeros(gray.shape, np.uint8)
    new_image = cv2.drawContours(mask, [location], 0, 255, -1)
    new_image = cv2.bitwise_and(image_array, image_array, mask=mask)

    # Extract the license plate
    (x, y) = np.where(mask == 255)
    (x1, y1) = (np.min(x), np.min(y))
    (x2, y2) = (np.max(x), np.max(y))
    final_plate = gray[x1:x2+1, y1:y2+1]

    # Convert the result to byte array and return
    _, buffer = cv2.imencode('.png', final_plate)
    return buffer.tobytes()

