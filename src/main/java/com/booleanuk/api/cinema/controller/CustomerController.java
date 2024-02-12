package com.booleanuk.api.cinema.controller;

import com.booleanuk.api.cinema.model.CustomResponse;
import com.booleanuk.api.cinema.model.Customer;
import com.booleanuk.api.cinema.model.Screening;
import com.booleanuk.api.cinema.model.Ticket;
import com.booleanuk.api.cinema.repository.CustomerRepository;
import com.booleanuk.api.cinema.repository.ScreeningRepository;
import com.booleanuk.api.cinema.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("customers")
public class CustomerController {

	@Autowired
	private TicketRepository ticketRepository;

	@Autowired
	private ScreeningRepository screeningRepository;
	@Autowired
	private CustomerRepository customerRepository;

	@GetMapping
	public CustomResponse<List<Customer>> getAll() {

		return new CustomResponse<>("success", customerRepository.findAll());
	}

	@PostMapping
	public ResponseEntity<CustomResponse<?>> create(@RequestBody Customer customer) {
		if (customer.getName() == null || customer.getEmail() == null || customer.getPhone() == null) {
			CustomResponse<Object> errorResponse = new CustomResponse<>("error", Collections.singletonMap("message", "bad request"));
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		Customer newCustomer = new Customer(customer.getName(), customer.getEmail(), customer.getPhone());
		Customer savedCustomer = customerRepository.save(newCustomer);

		CustomResponse<Customer> response = new CustomResponse<>("success", savedCustomer);
		return ResponseEntity.ok(response);
	}

	@PostMapping("{customer_id}/screenings/{screening_id}")
	public ResponseEntity<CustomResponse<?>> createTicket(@PathVariable int customer_id, @PathVariable int screening_id, @RequestBody Ticket ticket) {
		Screening screening = screeningRepository.findById(screening_id).orElse(null);
		if(screening==null){
			return new ResponseEntity<>(new CustomResponse<>("error", Collections.singletonMap("message", "not found")), HttpStatus.NOT_FOUND);

		}
		Customer customer = customerRepository.findById(customer_id).orElse(null);
		if(customer==null){
			return new ResponseEntity<>(new CustomResponse<>("error", Collections.singletonMap("message", "not found")), HttpStatus.NOT_FOUND);

		}
		Ticket newTicket = new Ticket(screening, customer, ticket.getNumSeats());

		return new ResponseEntity<>(new CustomResponse<>("success",ticketRepository.save(newTicket)), HttpStatus.CREATED);
	}

	@GetMapping("{customer_id}/screenings/{screening_id}")
	public ResponseEntity<CustomResponse<?>> getTickets(@PathVariable int customer_id, @PathVariable int screening_id) {
		List<Ticket> tickets = ticketRepository.findByCustomer_IdAndScreening_Id(customer_id, screening_id);

		if (tickets.isEmpty()) {
			return new ResponseEntity<>(new CustomResponse<>("error", Collections.singletonMap("message", "not found")), HttpStatus.NOT_FOUND);
		}

		return ResponseEntity.ok(new CustomResponse<>("success", tickets));
	}

	@GetMapping("{id}")
	public ResponseEntity<CustomResponse<?>> get(@PathVariable int id) {
		Customer customer = customerRepository.findById(id).orElse(null);
		if (customer == null) {
			return new ResponseEntity<>(new CustomResponse<>("error", Collections.singletonMap("message", "not found")), HttpStatus.NOT_FOUND);
		}
		return ResponseEntity.ok(new CustomResponse<>("success", customer));
	}

	@PutMapping("{id}")
	public ResponseEntity<CustomResponse<?>> update(@PathVariable int id, @RequestBody Customer customer) {
		Customer newCustomer = customerRepository.findById(id).orElse(null);
		if (newCustomer == null) {
			return new ResponseEntity<>(new CustomResponse<>("error", Collections.singletonMap("message", "not found")), HttpStatus.NOT_FOUND);

		}
		newCustomer.setName(customer.getName());
		newCustomer.setEmail(customer.getEmail());
		newCustomer.setPhone(customer.getPhone());
		return new ResponseEntity<>(new CustomResponse<>("success", customerRepository.save(newCustomer)), HttpStatus.CREATED);
	}

	@DeleteMapping("{id}")
	public ResponseEntity<CustomResponse<?>> delete(@PathVariable int id) {
		Customer customer = customerRepository.findById(id).orElse(null);
		if (customer == null) {
			return new ResponseEntity<>(new CustomResponse<>("error", Collections.singletonMap("message", "not found")), HttpStatus.NOT_FOUND);

		}
		customerRepository.delete(customer);
		return ResponseEntity.ok(new CustomResponse<>("success", customer));

	}

}