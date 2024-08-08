package dev.aziz.course.controllers;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import dev.aziz.course.entities.Course;
import dev.aziz.course.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    // GET all
    @GetMapping
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    // GET by id
    @GetMapping("/{id}")
    public ResponseEntity<Course> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }

    // POST create
    @PostMapping
    public ResponseEntity<Course> createCourse(@RequestBody Course course) {
        Course saved = courseService.addCourse(course);
        return ResponseEntity.ok(saved);
    }

    // PATCH edit by id
    @PutMapping("/{id}")
    public ResponseEntity<Course> updateCourseById(@PathVariable Long id, @RequestBody Course course) {
        Course updatedCourse = courseService.updateCourseById(id, course);
        return ResponseEntity.ok(updatedCourse);
    }

    // DELETE by id
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCourseById(@PathVariable Long id) {
        courseService.deleteCourseById(id);
        return ResponseEntity.ok("Course deleted.");
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<InputStreamResource> downloadCourse(@PathVariable Long id) throws IOException {
        // Generate or retrieve the PDF file content for the course
        Course course = courseService.getCourseById(id);
        byte[] pdfContent = generatePdfForCourse(course);

        // Convert the byte array into an InputStreamResource
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(pdfContent);
        InputStreamResource resource = new InputStreamResource(byteArrayInputStream);

        // Set the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=course_" + id + ".pdf");

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdfContent.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private byte[] generatePdfForCourse(Course course) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        // Initialize PDF writer and document
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add content to the PDF
        document.add(new Paragraph("Course ID: " + course.getId()));
        document.add(new Paragraph("Title: " + course.getTitle()));
        document.add(new Paragraph("Description: " + course.getDescription()));
        document.add(new Paragraph("Photo Link: " + course.getPhotoLink()));

        // Close document
        document.close();

        return outputStream.toByteArray();
    }

}
