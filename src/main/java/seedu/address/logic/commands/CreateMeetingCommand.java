package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Objects;

import seedu.address.logic.commands.exceptions.CommandException;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.Model;
import seedu.address.model.meeting.Meeting;
import seedu.address.model.meeting.exceptions.DuplicateMeetingException;
import seedu.address.model.person.Person;
import seedu.address.model.person.exceptions.DuplicatePersonException;
import seedu.address.model.person.exceptions.PersonNotFoundException;

/**
 * Creates a meeting with a person in the address book
 */
public class CreateMeetingCommand extends Command {

    public static final String COMMAND_WORD = "meet";

    public static final String MESSAGE_USAGE = COMMAND_WORD
        + ": Schedules a new meeting between you and another user.\n"
        + "Parameters: Names of people to meet (from address book, split names by }} ) ;;;\n"
        + "Title of meeting;;;\n"
        + "Date and time of meeting (in dd-MM-yyyy HHmm format);;;\n"
        + "location of meeting\n"
        + "Example: " + COMMAND_WORD + " Alex Yeoh }} Bernice Yu ;;; Study Session ;;; 06-10-2022 2015 ;;; UTown";

    public static final String MESSAGE_CREATE_MEETING_SUCCESS = "Created meeting with: \n%1$s";

    public static final String INCORRECT_NUMBER_OF_ARGUMENTS = "Make sure you have entered "
        + "the correct amount of information correctly separated!";

    public static final String PERSON_NOT_FOUND = "Oops! The person you are meeting with doesn't exist "
        + "in the address book. Do check if you have entered their name correctly.";

    public static final String DUPLICATE_MEETINGS = "Oops! Seems that you have already scheduled to meet "
        + "the same person(s) at the same time";

    public static final String DUPLICATE_PERSON_TO_MEET = "It looks like you are adding the same "
        + "person to a meeting twice!";

    private final String meetingInfo;

    public CreateMeetingCommand(String meetingInfo) {
        this.meetingInfo = meetingInfo;
    }

    /**
     * Converts an ArrayList of Persons to a string of the Persons' names and tags
     *
     * @param arrayOfPeopleToMeet ArrayList of Persons to meet in the Meeting
     * @return a String of all the Persons' names and tags, with every person separated by a line break
     */
    public static String peopleToNameAndTagList(ArrayList<Person> arrayOfPeopleToMeet) {
        StringBuilder output = new StringBuilder();
        for (Person personToMeet : arrayOfPeopleToMeet) {
            String toAppend = String.format("%1$s %2$s \n", personToMeet.getName(), personToMeet.getTags());
            output.append(toAppend);
        }
        return output.toString();
    }

    @Override
    public CommandResult execute(Model model) throws CommandException {

        try {
            requireNonNull(model);
            String[] newMeetingInformation = this.meetingInfo.split(";;;");
            String[] peopleToMeet = newMeetingInformation[0].strip().split("}}");
            String meetingTitle = newMeetingInformation[1].strip();
            String meetingDateAndTime = newMeetingInformation[2].strip();
            String meetingLocation = newMeetingInformation[3].strip();

            if ((Objects.equals(meetingTitle, "")) || (Objects.equals(meetingLocation, ""))) {
                return new CommandResult(INCORRECT_NUMBER_OF_ARGUMENTS);
            }

            ArrayList<Person> arrayOfPeopleToMeet = Meeting.convertNameToPerson(model, peopleToMeet);

            Meeting newMeeting = model.createNewMeeting(arrayOfPeopleToMeet, meetingTitle,
                meetingDateAndTime, meetingLocation);
            model.addMeeting(newMeeting);

            return new CommandResult(
                String.format(MESSAGE_CREATE_MEETING_SUCCESS, peopleToNameAndTagList(arrayOfPeopleToMeet))
                    + String.format("For: %1$s\n", meetingTitle)
                    + String.format("On: %1$s\n", newMeeting.getDateAndTime())
                    + String.format("At: %1$s\n", meetingLocation)
            );

        } catch (ParseException | java.text.ParseException e) {
            return new CommandResult(e.getMessage());

        } catch (IndexOutOfBoundsException e) {
            return new CommandResult(CreateMeetingCommand.INCORRECT_NUMBER_OF_ARGUMENTS);

        } catch (PersonNotFoundException e) {
            return new CommandResult(CreateMeetingCommand.PERSON_NOT_FOUND);

        } catch (DuplicateMeetingException e) {
            return new CommandResult(CreateMeetingCommand.DUPLICATE_MEETINGS);

        } catch (DuplicatePersonException e) {
            return new CommandResult(CreateMeetingCommand.DUPLICATE_PERSON_TO_MEET);
        }
    }

    @Override
    public boolean equals(Object other) {
        return other == this // short circuit if same object
            || (other instanceof CreateMeetingCommand // instanceof handles nulls
            && this.meetingInfo.equals(((CreateMeetingCommand) other).meetingInfo)); // state check
    }

}
