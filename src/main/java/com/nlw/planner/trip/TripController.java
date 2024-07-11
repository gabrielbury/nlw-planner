package com.nlw.planner.trip;

import com.nlw.planner.activity.ActivityData;
import com.nlw.planner.activity.ActivityRequestPayload;
import com.nlw.planner.activity.ActivityResponse;
import com.nlw.planner.activity.ActivityService;
import com.nlw.planner.link.LinkData;
import com.nlw.planner.link.LinkRequestPayload;
import com.nlw.planner.link.LinkResponse;
import com.nlw.planner.link.LinkService;
import com.nlw.planner.participant.ParticipantCreateResponse;
import com.nlw.planner.participant.ParticipantData;
import com.nlw.planner.participant.ParticipantRequestPayload;
import com.nlw.planner.participant.ParticipantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {
    @Autowired
    private TripService tripService;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private ParticipantService participantService;

    @Autowired
    private LinkService linkService;


    @PostMapping
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload tripRequestPayload) {
        Trip newTrip = new Trip(tripRequestPayload);
        this.tripService.createTrip(newTrip, tripRequestPayload.emails_to_invite());

        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id) {
        Optional<Trip> trip = this.tripService.getTrip(id);
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload tripRequestPayload) {
        var tripToUpdate = new Trip(tripRequestPayload);
        Optional<Trip> trip = this.tripService.updateTrip(id, tripToUpdate);

        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> confirmTrip(@PathVariable UUID id) {
        Optional<Trip> trip = this.tripService.confirmTrip(id);
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/activities")
    public ResponseEntity<ActivityResponse> registerActivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload) {
        var activityResponse = this.tripService.registerActivity(id, payload);
        return activityResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id) {
        var activities = this.activityService.getAllActivitiesFromId(id);
        return ResponseEntity.ok(activities);
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id) {
        List<ParticipantData> participantList = this.participantService.getAllParticipantsFromTrip(id);
        return ResponseEntity.ok(participantList);
    }

    @PostMapping("/{id}/invite")
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        var participantInvite = this.tripService.inviteParticipant(id, payload);
        return participantInvite.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/links")
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload) {
        Optional<LinkResponse> registeredLink = this.tripService.registerLink(id, payload);
        return registeredLink.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id) {
        var links = this.linkService.getAllLinksFromTrip(id);
        return ResponseEntity.ok(links);
    }
}
