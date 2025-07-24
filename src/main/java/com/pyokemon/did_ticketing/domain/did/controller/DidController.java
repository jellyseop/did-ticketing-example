package com.pyokemon.did_ticketing.domain.did.controller;

import com.pyokemon.did_ticketing.common.dto.ApiResponseDto;
import com.pyokemon.did_ticketing.domain.did.model.DidDocument;
import com.pyokemon.did_ticketing.domain.did.service.DidService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/did")
@RequiredArgsConstructor
@Tag(name = "DID API", description = "DID 관련 API")
@SecurityRequirement(name = "bearerAuth")
public class DidController {
    
    private final DidService didService;
    
    @GetMapping("/{did}")
    public ResponseEntity<ApiResponseDto<DidDocument>> getDidDocument(
            @Parameter(description = "DID 식별자", required = true, example = "did:pyokemon:123456789") 
            @PathVariable String did) {
        DidDocument document = didService.resolveDid(did);
        return ResponseEntity.ok(ApiResponseDto.createOk(document));
    }
} 