package br.com.person.api.controller;

import br.com.person.api.dto.SourceCodeResponseDTO;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class SourceCodeController {

    private static final Logger logger = LoggerFactory.getLogger(SourceCodeController.class);

    @ApiOperation(value = "Busca url do código fonte do projeto.", produces = "application/json")
    @ApiResponses({
            @ApiResponse(code = 401, message = "Não autorizado"),
            @ApiResponse(code = 202, message = "Retorno do Url.")
    })
    @GetMapping({"v1/source", "v2/source"})
    public ResponseEntity<?> getSourceCode() {
        logger.info("GET - Source Code, getSourceCode");
        String gitHubUrl = "https://github.com/MohShad/rest-api-person/tree/master";
        return new ResponseEntity(new SourceCodeResponseDTO("Url do código fonte do projeto no github.", gitHubUrl), HttpStatus.ACCEPTED);
    }
}
