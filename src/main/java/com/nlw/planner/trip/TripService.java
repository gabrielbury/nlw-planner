package com.nlw.planner.trip;

import com.nlw.planner.activity.ActivityRepository;
import com.nlw.planner.activity.ActivityRequestPayload;
import com.nlw.planner.activity.ActivityResponse;
import com.nlw.planner.activity.ActivityService;
import com.nlw.planner.exception.InvalidDateSelectedException;
import com.nlw.planner.link.LinkRequestPayload;
import com.nlw.planner.link.LinkResponse;
import com.nlw.planner.link.LinkService;
import com.nlw.planner.participant.ParticipantCreateResponse;
import com.nlw.planner.participant.ParticipantRequestPayload;
import com.nlw.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TripService {

    static String INVALID_DATE_MESSAGE = "As datas selecionadas para a viagem são inválidas.";
    static String INVALID_ACTIVITY_DATE_MESSAGE = "A data selecionada para a atividade não está dentro do período da viagem.";

    @Autowired
    private TripRepository tripRepository;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private LinkService linkService;

    public Trip createTrip(Trip trip, List<String> emailsToInvite){
        // Validar datas
        this.validateTripDates(trip);
        this.tripRepository.save(trip);
        this.participantService.registerParticipantsToTrip(emailsToInvite, trip);
        return trip;
    }

    public Optional<Trip> getTrip(UUID id) {
        return this.tripRepository.findById(id);
    }

    public Optional<Trip> updateTrip(UUID id, Trip tripToUpdate) {
        Optional<Trip> trip = this.tripRepository.findById(id);
        if(trip.isPresent()) {
            Trip rawTrip = trip.get();

            validateTripDates(tripToUpdate);

            rawTrip.setEndsAt(tripToUpdate.getEndsAt());
            rawTrip.setStartsAt(tripToUpdate.getStartsAt());
            rawTrip.setDestination(tripToUpdate.getDestination());
            this.tripRepository.save(rawTrip);

        }
        return trip;
    }

    public Optional<Trip> confirmTrip(UUID id) {
        Optional<Trip> trip = this.tripRepository.findById(id);
        if(trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setIsConfirmed(true);
            this.participantService.triggerConfirmationEmailToParticipants(id);

            this.tripRepository.save(rawTrip);
        }
        return trip;
    }

    public Optional<ActivityResponse> registerActivity(UUID id, ActivityRequestPayload activityRequestPayload) {
        var trip = this.getTrip(id);
         if(trip.isPresent()){
            Trip rawTrip = trip.get();

            validateActivityDate(rawTrip, LocalDateTime.parse(activityRequestPayload.occurs_at(), DateTimeFormatter.ISO_DATE_TIME));

            ActivityResponse activityResponse = this.activityService.registerActivity(activityRequestPayload, rawTrip);
            return Optional.of(activityResponse);
        }
         return Optional.empty();
    }

    private void validateDateInterval(LocalDateTime startDate, LocalDateTime endDate, String errorMessage) {
        if(startDate.compareTo(endDate) >= 0) {
            throw new InvalidDateSelectedException(errorMessage);
        }
    }

    private void validateTripDates(Trip trip) {
        validateDateInterval(trip.getStartsAt(), trip.getEndsAt(), INVALID_DATE_MESSAGE);
    }

    private void validateActivityDate(Trip trip, LocalDateTime activityDate) {
        validateDateInterval(trip.getStartsAt(), activityDate, INVALID_ACTIVITY_DATE_MESSAGE);
        validateDateInterval(activityDate, trip.getEndsAt(), INVALID_ACTIVITY_DATE_MESSAGE);
    }

    public Optional<ParticipantCreateResponse> inviteParticipant(UUID id, ParticipantRequestPayload payload) {
        Optional<Trip> trip = this.tripRepository.findById(id);
        if(trip.isPresent()){
            Trip rawTrip = trip.get();
            ParticipantCreateResponse participantResponse  = this.participantService.registerParticipantToTrip(payload.email(), rawTrip);

            if(rawTrip.getIsConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

            return Optional.of(participantResponse);
        }
        return Optional.empty();
    }

    public Optional<LinkResponse> registerLink(UUID id, LinkRequestPayload payload) {
        Optional<Trip> trip = this.tripRepository.findById(id);
        if(trip.isPresent()){
            Trip rawTrip = trip.get();
            LinkResponse linkResponse  = this.linkService.registerLink(payload, rawTrip);
            return Optional.of(linkResponse);
        }
        return Optional.empty();
    }
}
