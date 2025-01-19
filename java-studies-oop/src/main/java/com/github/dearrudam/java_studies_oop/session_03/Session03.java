package com.github.dearrudam.java_studies_oop.session_03;

import static java.util.Objects.requireNonNull;
import static java.util.Optional.ofNullable;

public class Session03 {

    public static void main(String[] args) {

        Notification notification = null;
        try {
            notification = Notification
                    .builder()
                    .title("builders are amazing")
                    .message("intelligent builders helps the developers clients")
                    .recipient("everyone")
                        .highPriority(true)
                        .highPriority(false)
                        .type(Type.INFO)
                        .attachment("read my article here: https://foojay.io/today/make-the-life-of-your-developer-clients-easier-with-smart-builders/ ")
                    .build();
        } catch (InvalidArguments e) {
            // only something went wrong with the arguments
            throw new RuntimeException(e);
        }

        System.out.println(notification);
    }


    public static enum Type {
        GENERAL,
        INFO,
        WARNING,
        ERROR;
    }


    public static record Notification(
            String title,
            String message,
            String recipient,
            boolean highPriority,
            Type type,
            String attachment) {


        public Notification {
            requireNonNull(title, "Title is required");
            requireNonNull(message, "Message is required");
            requireNonNull(recipient, "Recipient is required");
            requireNonNull(type, "Type is required");
        }

        public Notification(String title, String message, String recipient) {
            this(title, message, recipient, false, Type.GENERAL, null);
        }

        public static NotificationBuilder builder() {
            return new NotificationBuilder();
        }

    }

    public static class NotificationBuilder {

        public NotificationBuilderWithTitle title(String title) {
            return new NotificationBuilderWithTitle(title);
        }
    }

    public static record NotificationBuilderWithTitle(String title) {

        public NotificationBuilderWithTitleMessage message(String message) {
            return new NotificationBuilderWithTitleMessage(title, message);
        }

    }

    public static record NotificationBuilderWithTitleMessage(String title, String message) {

        public NotificationBuilderOptionals recipient(String recipient) {
            return new NotificationBuilderOptionals(title, message, recipient);
        }
    }

    public static record NotificationBuilderOptionals(
            String title,
            String message,
            String recipient,
            boolean highPriority,
            Type type,
            String attachment
    ) {


        public NotificationBuilderOptionals(String title, String message, String recipient) {
            this(title, message, recipient, false, Type.GENERAL, null);
        }

        public NotificationBuilderOptionals highPriority(boolean highPriority) {
            return new NotificationBuilderOptionals(
                    title,
                    message,
                    recipient,
                    highPriority,
                    type,
                    attachment
            );
        }

        public NotificationBuilderOptionals type(Type type) {
            return new NotificationBuilderOptionals(
                    title,
                    message,
                    recipient,
                    highPriority,
                    ofNullable(type).orElse(Type.GENERAL),
                    attachment
            );
        }

        public NotificationBuilderOptionals attachment(String attachment) {
            return new NotificationBuilderOptionals(
                    title,
                    message,
                    recipient,
                    highPriority,
                    type,
                    attachment
            );
        }

        public Notification build() throws InvalidArguments {
            try {
                return new Notification(
                        title,
                        message,
                        recipient,
                        highPriority,
                        type,
                        attachment
                );
            }catch (Exception ex){
                throw new InvalidArguments(ex);
            }

        }
    }

    public static class InvalidArguments extends Exception{
        public InvalidArguments(Throwable cause) {
            super(cause);
        }
    }
}
