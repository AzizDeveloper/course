package dev.aziz.course.services;

public class EmailUtils {

    public static String getEmailMessage(String name, String host, String activationCode) {
        return "Hello " + name + ",\n\nYour new account has been created. Please click the link below to verify your account. \n\n" +
                getVerificationUrl(host, activationCode) + "\n\nThe support Team";
    }

    public static String getEmailMessageWithCode(String name, String host, String secretCode) {
        return "Hello " + name + ",\n\nYour can create new password. Please hit this endpoint \n\n"
                + host +"/new-password?secretCode=" + secretCode + "&newPassword=" + " write your new password. and send it. \n\n"
                + "\n\nThe support Team";
    }

    public static String getVerificationUrl(String host, String activationCode) {
        return host + "/verify?activationCode=" + activationCode;
    }
}
