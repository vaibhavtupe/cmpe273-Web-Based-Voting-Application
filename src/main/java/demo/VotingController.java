package demo;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import dao.PollDAO;



@RestController
@RequestMapping("/api/v1/*")
public class VotingController {
	
	
	
	ControllerOperations cop= new ControllerOperations();
	
	
	
    // 1 POST : Create Moderator
	
	@RequestMapping(value="/moderators", method=RequestMethod.POST)
	public ResponseEntity<Moderator> createModerator(@Valid @RequestBody Moderator mod){
		
		mod=cop.createModerator(mod);
	
		return new ResponseEntity<Moderator>(mod, HttpStatus.CREATED);
		
	}
	
	
	// 2 GET : View Moderator
	
	@RequestMapping(value="/moderators/{moderator_id}", method=RequestMethod.GET)
	public ResponseEntity<Moderator> getModerator(@PathVariable int moderator_id){
		
		Moderator mod=cop.getModerator(moderator_id);
		
		if(mod != null){
			
			return new ResponseEntity<Moderator>(mod, HttpStatus.OK);
		}else{
		
	     return new ResponseEntity<Moderator>(HttpStatus.NOT_FOUND);
		}
		
	}
	
	
	// 3 PUT : Update Moderator
	
	@RequestMapping(value="/moderators/{moderator_id}", method=RequestMethod.PUT)
	public ResponseEntity<Moderator> updateModerator(@PathVariable int moderator_id,@Valid @RequestBody Moderator mod1){
			
		return new ResponseEntity<Moderator>(cop.updateModerator(moderator_id,mod1), HttpStatus.CREATED);
		
	}

	
	
	 // 4 POST : Create a new Poll
	
		@RequestMapping(value="/moderators/{moderator_id}/polls", method=RequestMethod.POST)
		public ResponseEntity<Poll> createPoll(@PathVariable int moderator_id,@Valid @RequestBody Poll poll){
			
			Poll pollf= cop.insertPoll(moderator_id, poll);
			
			return new ResponseEntity<Poll>(pollf, HttpStatus.CREATED);
			
		}
		
		
	 // 5 GET : list poll details for particular poll_id without results
		
		@RequestMapping(value="/polls/{poll_id}", method=RequestMethod.GET)
		public ResponseEntity<Poll> getPoll(@PathVariable String poll_id){
			
			Poll poll= cop.viewPollWithoutResult(poll_id);
			if(poll!=null){
				
				return new ResponseEntity<Poll>(poll, HttpStatus.OK);
				
			}else{
			
		     return new ResponseEntity<Poll>(HttpStatus.NOT_FOUND);	
			}
		}
		
		
	 // 6 GET  : list poll with result
		
		@RequestMapping(value="/moderators/{moderator_id}/polls/{poll_id}", method=RequestMethod.GET)
		public ResponseEntity<PollDAO> getPollWithResult(@PathVariable String poll_id,@PathVariable int moderator_id){
			
			PollDAO poll= cop.viewPollWithResult(moderator_id, poll_id);
			if(poll!=null){
				
			 return new ResponseEntity<PollDAO>(poll, HttpStatus.OK);
			 
			}else
			{
		     return new ResponseEntity<PollDAO>(HttpStatus.NOT_FOUND);	
			}
		}
	
		
    // 7 GET  : list all polls
		
		@RequestMapping(value="/moderators/{moderator_id}/polls", method=RequestMethod.GET)
		public ResponseEntity<List<PollDAO>> getAllPolls(@PathVariable int moderator_id){
			
			
			List<PollDAO> result= new LinkedList<PollDAO>();
			
			result=cop.viewAllPolls(moderator_id);
			if(result!=null){
			
			 return new ResponseEntity<List<PollDAO>>(result,HttpStatus.OK);
			 
			}
			else{
		     return new ResponseEntity<List<PollDAO>>(HttpStatus.NOT_FOUND);
			}
		}
		
		
		// 8 DELETE : delete a poll
		
		@RequestMapping(value="/moderators/{moderator_id}/polls/{poll_id}", method=RequestMethod.DELETE)
		public ResponseEntity deletePoll(@PathVariable int moderator_id, @PathVariable String poll_id){
			
			
			cop.deletePoll(poll_id);
			
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			
		}
		
		
		// 9 PUT : Vote a poll
		
		@RequestMapping(value="/polls/{poll_id}", method=RequestMethod.PUT)
		public ResponseEntity votePoll(@PathVariable String poll_id,@Valid @RequestParam("choice") int choice_index){
							
				cop.votePoll(poll_id, choice_index);	
			
			return new ResponseEntity(HttpStatus.NO_CONTENT);
			
		}

		
	
}



