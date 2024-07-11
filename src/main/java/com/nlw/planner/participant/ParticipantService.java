package com.nlw.planner.participant;

import com.nlw.planner.trip.Trip;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class ParticipantService {

    @Autowired
    private ParticipantRepository repository;

    public void registerParticipantsToTrip(List<String> participantsToInvite, Trip trip) {
        List<Participant> participants = participantsToInvite
                .stream()
                .map(email -> new Participant(email, trip))
                .toList();
        this.repository.saveAll(participants);

        log.info(participants.get(0).getId().toString());
    }

    public ParticipantCreateResponse registerParticipantToTrip(String email, Trip trip) {
        Participant newParticipant = new Participant(email, trip);
        this.repository.save(newParticipant);

        return new ParticipantCreateResponse(newParticipant.getId());
    }

    public void triggerConfirmationEmailToParticipants(UUID tripId) {
    }


    public void triggerConfirmationEmailToParticipant(String email) {
    }

    public List<ParticipantData> getAllParticipantsFromTrip(UUID tripId) {
        return this.repository.findByTripId(tripId)
                .stream()
                .map(participant -> new ParticipantData(
                        participant.getId(),
                        participant.getName(),
                        participant.getEmail(),
                        participant.getIsConfirmed()))
                .toList();
    }

}
