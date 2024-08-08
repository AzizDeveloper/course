package dev.aziz.course.services;

import dev.aziz.course.config.UserAuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConfirmationService {

    private final UserAuthProvider userAuthProvider;



}
