package dev.aziz.course.services;

import dev.aziz.course.entities.Course;
import dev.aziz.course.exceptions.AppException;
import dev.aziz.course.repositories.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Course getCourseById(Long id) {
        return courseRepository.findCourseById(id)
                .orElseThrow(() -> new AppException("Course not found.", HttpStatus.NOT_FOUND));
    }

    public Course addCourse(Course course) {
        return courseRepository.save(course);
    }

    public void deleteCourseById(Long id) {
        courseRepository.deleteById(id);
    }

    public Course updateCourseById(Long id, Course course) {
        Course courseById = getCourseById(id);
        courseById.setTitle(course.getTitle());
        courseById.setDescription(course.getDescription());
        courseById.setPhotoLink(course.getPhotoLink());
        return courseRepository.save(courseById);
    }
}
