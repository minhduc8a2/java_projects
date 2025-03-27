POST /upload HTTP/1.1
Host: localhost:8080
Content-Type: multipart/form-data; boundary=----WebKitFormBoundary12345
Content-Length: 123456

------WebKitFormBoundary12345
Content-Disposition: form-data; name="file"; filename="example.txt"
Content-Type: text/plain

<FILE CONTENT HERE>
------WebKitFormBoundary12345--
