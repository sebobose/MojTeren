package org.zavrsni.backend.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.zavrsni.backend.reservation.dto.ReservationDTO;

@Service
@RequiredArgsConstructor
public class MailMessages {

    private final JavaMailSender mailSender;

    @Value("${MAIL_USER}")
    private String mailAlert;

    @Value("${MAIL_ADMIN}")
    private String mailAdmin;

    public static final String SPORT_CENTER_REQUEST_SUBJECT = "Zahtjev za dodavanje sportskog centra";
    public static final String SPORT_CENTER_REQUEST_APPROVED_SUBJECT = "Vaš sportski centar je odobren";
    public static final String SPORT_CENTER_REQUEST_DECLINED_SUBJECT = "Vaš sportski centar je odbijen";
    public static final String SPORT_CENTER_CREATED_SUBJECT = "Vaš sportski centar je dodan";
    public static final String SPORT_CENTER_UPDATED_SUBJECT = "Vaš sportski centar je ažuriran";
    public static final String SPORT_CENTER_DEACTIVATED_SUBJECT = "Vaš sportski centar je deaktiviran";
    public static final String RESERVATION_CANCELED_SUBJECT = "Vaša rezervacija je otkazana";
    public static final String MAIL_START = "<p>Poštovani/a,</br></br></p>";
    public static final String MAIL_END = "</br><p>Vaša MojTeren ekipa</p>";

    public void sportCenterRequest(String email, String sportCenterName) throws MessagingException {
        String message = MAIL_START
                + "<p>Korisnik " + email + " je zatražio dodavanje sportskog centra " + sportCenterName + " na mojTeren platformu.</p>"
                + MAIL_END;
        this.sendMail(mailAdmin, SPORT_CENTER_REQUEST_SUBJECT, message);
    }

    public void sportCenterCreated(String email, String sportCenterName) throws MessagingException {
        String message = MAIL_START
                + "<p>Vaš sportski centar " + sportCenterName + " je dodan na mojTeren platformu od strane administratora.</p>"
                + MAIL_END;
        this.sendMail(email, SPORT_CENTER_CREATED_SUBJECT, message);
    }

    public void sportCenterUpdated(String email, String sportCenterName) throws MessagingException {
        String message = MAIL_START
                + "<p>Vaš sportski centar " + sportCenterName + " je ažuriran od strane administratora.</p>"
                + MAIL_END;
        this.sendMail(email, SPORT_CENTER_UPDATED_SUBJECT, message);
    }

    public void sportCenterDeactivated(String email, String sportCenterName, String reason) throws MessagingException {
        String message = MAIL_START
                + "<p>Vaš sportski centar " + sportCenterName + " je deaktiviran od strane administratora.</p>"
                + "<p>Razlog deaktivacije: " + reason + "</p>"
                + MAIL_END;
        this.sendMail(email, SPORT_CENTER_DEACTIVATED_SUBJECT, message);
    }

    public void sportCenterDeactivatedByOwner(String email, String sportCenterName, String reason) throws MessagingException {
        String message = MAIL_START
                + "<p>Korisnik " + email + "je deaktivirao sportski centar " + sportCenterName + " </p>"
                + "<p>Razlog deaktivacije: " + reason + "</p>"
                + MAIL_END;
        this.sendMail(mailAdmin, SPORT_CENTER_DEACTIVATED_SUBJECT, message);
    }

    public void sportCenterRequestDeclined(String email, String reason) throws MessagingException {
        String message = MAIL_START
                + "<p>Vaš zahtjev za dodavanje sportskog centra je odbijen.</p>"
                + "<p>Razlog odbijanja: " + reason + "</p>"
                + MAIL_END;
        this.sendMail(email, SPORT_CENTER_REQUEST_DECLINED_SUBJECT, message);
    }

    public void sportCenterRequestApproved(String email) throws MessagingException {
        String message = MAIL_START
                + "<p>Vaš zahtjev za dodavanje sportskog centra je odobren. Sada možete dodavati terene na svoj sportski centar.</p>"
                + MAIL_END;
        this.sendMail(email, SPORT_CENTER_REQUEST_APPROVED_SUBJECT, message);
    }

    public void reservationCanceledByOwner(ReservationDTO reservationDTO, String reason, String user) throws MessagingException {
        String message = MAIL_START
                + "<p>Vaša rezervacija " + reservationDTO.getDate() + " "
                + reservationDTO.getStartTime() +  "-" + reservationDTO.getEndTime() + " u sportskom centru "
                + reservationDTO.getSportCenterName() + " na terenu " + reservationDTO.getField()
                + " je otkazana od strane " + user + ".</p>"
                + "<p>Razlog otkazivanja: " + reason + "</p>"
                + MAIL_END;
        this.sendMail(reservationDTO.getUser().getEmail(), RESERVATION_CANCELED_SUBJECT, message);
    }

    public void reservationCanceledByAthlete(ReservationDTO reservationDTO, String ownerMail, String reason, String user) throws MessagingException {
        String message = MAIL_START
                + "<p>Rezervacija na vašem terenu je otkazana" + user + ".</p>"
                + "<p>Detalji rezervacije:</p>"
                + "<p>Ime i prezime sportaša: " + reservationDTO.getUser().getFirstName() + " " + reservationDTO.getUser().getLastName() + "</p>"
                + "<p>Email: " + reservationDTO.getUser().getEmail() + "</p>"
                + "<p>Kontakt: " + reservationDTO.getUser().getContact() + "</p>"
                + "<p>Datum: " + reservationDTO.getDate() + "</p>"
                + "<p>Vrijeme: " + reservationDTO.getStartTime() +  "-" + reservationDTO.getEndTime() + "</p>"
                + "<p>Sportski centar: " + reservationDTO.getSportCenterName() + "</p>"
                + "<p>Teren: " + reservationDTO.getField() + "</p>"
                + "<p>Razlog otkazivanja: " + reason + "</p>"
                + MAIL_END;
        this.sendMail(ownerMail, RESERVATION_CANCELED_SUBJECT, message);
    }

    private void sendMail(String receiver, String subject, String message) throws MessagingException {
        System.out.println("from: " + mailAlert);
        System.out.println("receiver: " + receiver);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(mailAlert);
        helper.setTo(receiver);
        helper.setSubject(subject);
        helper.setText(message, true);
        mailSender.send(mimeMessage);
    }
}
