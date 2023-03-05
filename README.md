# EVENT TAG
This is a fully dockerized api that allows users to perform `CRUD` operations to an *Event* and *Attendees* as 
well as mark *Attendance* for these attendees.<br>

Once an `event` has been created, attendees can be added to a given `event`. Subsequently, the attendance of each `attendee` can 
be recorded - that is, what time they checked in and checked out of the event. <br>
The `Events` and `Attendees` have a `many -> many` relationship. <br><br>

The api encompasses the following layers: 
1. DAO layer - whose implementations are via `Slick ORM`. 
2. `PostgreSQL` Database layer *(dockerized)
3. Service layer
4. Akka typed Actors - whose sole purpose is to facilitate interaction between the *Service* layer with the *Routes*
5. Akka HTTP routes
