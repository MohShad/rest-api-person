package br.com.person.api.controller;

import br.com.person.api.dto.ApiResponseDTO;
import br.com.person.api.dto.PersonRequestDTO;
import br.com.person.api.dto.PersonRequestUpdateDTO;
import br.com.person.api.dto.PersonResponseSaveDTO;
import br.com.person.api.model.Person;
import br.com.person.api.repository.PersonRepository;
import br.com.person.api.service.PersonService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/person")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonController {

    private static final Logger logger = LoggerFactory.getLogger(PersonController.class);

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PersonService personService;

    @ApiOperation(value = "Cadastro pessoa", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 401, message = "Não autorizado"),
            @ApiResponse(code = 201, message = "A Pessoa foi cadastrado com sucesso.")
    })
    @PostMapping
    public ResponseEntity<?> registerPerson(
            @ApiParam(value = "Obejto person para criar pessoa em banco de dados.", required = true)
            @Valid @RequestBody PersonRequestDTO personRequestDTO) {
        logger.info("POST - Person-v1, registerPerson");
        try {
            if (personRepository.existsByCpf(personRequestDTO.getCpf())) {
                return new ResponseEntity(new ApiResponseDTO(false, "Existe pessoa registrado com CPF: " + personRequestDTO.getCpf()),
                        HttpStatus.CONFLICT);
            }
            Long id = personService.savePerson(personRequestDTO);

            return new ResponseEntity(new PersonResponseSaveDTO(true, "Pessoa registrado com sucesso.", id),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResponseEntity(new ApiResponseDTO(false, "Internal error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Busca de pessoa por Cpf.", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 401, message = "Não autorizado"),
            @ApiResponse(code = 202, message = "Consultar Pessoa por Cpf.")
    })
    @GetMapping("/getByCpf/{cpf}")
    public ResponseEntity<Person> getByCpf(
            @ApiParam(value = "Cpf da pessoa.", required = true)
            @PathVariable("cpf") String cpf) {
        logger.info("GET - Person-v1, getByCpf");
        try {
            if (!personRepository.existsByCpf(cpf)) {
                return new ResponseEntity(new ApiResponseDTO(false, "Não existe pessoa registrado com CPF: " + cpf),
                        HttpStatus.CONFLICT);
            }
            Person person = personService.getByCpf(cpf);
            return new ResponseEntity<Person>(person, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResponseEntity(new ApiResponseDTO(false, "Internal error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Verificar a existencia do Cpf.", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 401, message = "Não autorizado"),
            @ApiResponse(code = 202, message = "Verificar a existencia do Cpf.")
    })
    @GetMapping("/existCpf/{cpf}")
    public ResponseEntity<ApiResponseDTO> existCpf(
            @ApiParam(value = "Cpf da pessoa.", required = true)
            @PathVariable("cpf") String cpf) {
        logger.info("GET - Person-v1, existCpf");
        try {
            if (personRepository.existsByCpf(cpf)) {
                return new ResponseEntity(new ApiResponseDTO(true, "O cpf informado existe no banco de dados."),
                        HttpStatus.ACCEPTED);
            } else {
                return new ResponseEntity(new ApiResponseDTO(false, "O cpf informado não existe no banco de dados."),
                        HttpStatus.ACCEPTED);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResponseEntity(new ApiResponseDTO(false, "Internal error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Busca de pessoa por id.", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 401, message = "Não autorizado"),
            @ApiResponse(code = 202, message = "Consultar Pessoa por id.")
    })
    @GetMapping("/getById/{id}")
    public ResponseEntity<Person> getById(
            @ApiParam(value = "id da pessoa.", required = true)
            @PathVariable("id") Long id) {
        logger.info("GET - Person-v1, getById");
        try {
            Optional<Person> person = personRepository.findById(id);
            if (!person.isPresent()) {
                return new ResponseEntity(new ApiResponseDTO(false, "A pessoa com ID: " + id + " não foi encontrado"),
                        HttpStatus.CONFLICT);
            }
            Person pr = personService.getById(id);
            return new ResponseEntity<Person>(pr, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResponseEntity(new ApiResponseDTO(false, "Internal error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Busca Todas as pessoas.", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 401, message = "Não autorizado"),
            @ApiResponse(code = 202, message = "Consultar Pessoas.")
    })
    @GetMapping()
    public ResponseEntity<List<Person>> getAll(
            @ApiParam(value = "N/A.", required = false)
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "1") Integer size) {
        logger.info("GET - Person-v1, getAll");

        try {
            List<Person> personList = personService.getAll(page, size);
            return new ResponseEntity<List<Person>>(personList, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResponseEntity(new ApiResponseDTO(false, "Internal error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Atualizar registro da pessoa.", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 401, message = "Não autorizado"),
            @ApiResponse(code = 201, message = "Atualizar pessoa.")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Person> updateById(
            @PathVariable("id") Long id,
            @Valid @RequestBody PersonRequestUpdateDTO personRequestUpdateDTO
    ) {
        logger.info("PUT - Person-v1, updateById");
        try {

            Optional<Person> person = personRepository.findById(id);
            if (!person.isPresent())
                return new ResponseEntity(new ApiResponseDTO(false, "Não existe pessoa registrado com id: " + id),
                        HttpStatus.CONFLICT);
            ResponseEntity<Person> pr = personService.updateById(personRequestUpdateDTO, id);
            return new ResponseEntity(new PersonResponseSaveDTO(true, "A pessoa foi atualizado com sucesso,", id), HttpStatus.CREATED);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResponseEntity(new ApiResponseDTO(false, "Internal error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ApiOperation(value = "Excluir registro da pessoa.", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 401, message = "Não autorizado"),
            @ApiResponse(code = 202, message = "Atualizar pessoa.")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Person> deleteById(
            @PathVariable("id") Long id) {
        logger.info("DELETE - Person-v1, deleteById");
        try {

            Optional<Person> person = personRepository.findById(id);
            if (!person.isPresent())
                return new ResponseEntity(new ApiResponseDTO(false, "Não existe pessoa registrado com id: " + id),
                        HttpStatus.CONFLICT);
            ResponseEntity<Object> pr = personService.deleteById(id);
            return new ResponseEntity(new PersonResponseSaveDTO(true, "A pessoa foi excluida com sucesso,", id), HttpStatus.ACCEPTED);

        } catch (Exception e) {
            e.printStackTrace();
            logger.error(e.getMessage());
            return new ResponseEntity(new ApiResponseDTO(false, "Internal error: " + e.getMessage()),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
