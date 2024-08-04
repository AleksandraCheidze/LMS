package de.aittr.lmsbe.csv.controller.api;

import de.aittr.lmsbe.csv.dto.CsvImportDto;
import de.aittr.lmsbe.security.details.AuthenticatedUser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "CSVFileController", description = "Operations related to upload csv-files")
public interface CsvFileApi {

    @Operation(summary = "Upload csv file for mass user register")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File was successfully uploaded",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = String.class)))
            ),
            @ApiResponse(responseCode = "400", description = "File size or file extension does not meet the requirements. " +
                    "Requirements: file size up to 30MB, file *.csv"),
            @ApiResponse(responseCode = "403", description = "Access is forbidden"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping(value = "/upload-csv-file", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.OK)
    CsvImportDto csvFileUpload(@Parameter(content = @Content(mediaType = MediaType.APPLICATION_OCTET_STREAM_VALUE))
                               @RequestParam("file") MultipartFile file,
                               @AuthenticationPrincipal @Parameter(hidden = true) AuthenticatedUser currentUser) throws IOException;

}
