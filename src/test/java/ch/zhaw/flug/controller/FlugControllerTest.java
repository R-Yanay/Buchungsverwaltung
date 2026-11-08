package ch.zhaw.flug.controller;

import ch.zhaw.flug.model.Flug;
import ch.zhaw.flug.model.Passagier;
import ch.zhaw.flug.service.BuchungService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

/**
 * Controller-Tests fuer den FlugController.
 * Verwendet MockMvcTester (Spring Boot 4.x), um HTTP-Anfragen zu simulieren,
 * ohne einen echten Server zu starten. Der BuchungService wird gemockt.
 */
@WebMvcTest(FlugController.class)
class FlugControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private BuchungService buchungService;

    @Test
    void fluegeAnzeigen_gibtLeereListe() {
        given(buchungService.alleFluege()).willReturn(List.of());

        MvcTestResult result = mockMvc.get().uri("/fluege").exchange();
        assertThat(result).hasStatusOk();
        assertThat(result).body().asString().isEqualTo("[]");
    }

    @Test
    void fluegeAnzeigen_gibtFlugListe() {
        Flug flug = new Flug("Paris", LocalDateTime.of(2026, 12, 1, 10, 0), 5);
        given(buchungService.alleFluege()).willReturn(List.of(flug));

        MvcTestResult result = mockMvc.get().uri("/fluege").exchange();
        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$[0].ziel").isEqualTo("Paris");
        assertThat(result).bodyJson().extractingPath("$[0].maxKapazitaet").isEqualTo(5);
    }

    @Test
    void flugErstellen_gibtNeuenFlug() {
        Flug flug = new Flug("Paris", LocalDateTime.of(2026, 12, 1, 10, 0), 5);
        given(buchungService.flugErstellen(eq("Paris"), any(LocalDateTime.class), eq(5))).willReturn(flug);

        MvcTestResult result = mockMvc.post().uri("/fluege")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"ziel\":\"Paris\",\"abflug\":\"2026-12-01T10:00\",\"maxKapazitaet\":5}")
                .exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.ziel").isEqualTo("Paris");
        assertThat(result).bodyJson().extractingPath("$.maxKapazitaet").isEqualTo(5);
    }

    @Test
    void passagierEinbuchen_gibtErfolg() {
        Passagier passagier = new Passagier("Max", 30);
        given(buchungService.passagierEinbuchen(eq(1L), eq("Max"), eq(30))).willReturn(passagier);

        MvcTestResult result = mockMvc.post().uri("/fluege/1/buchen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Max\",\"alter\":30}")
                .exchange();
        assertThat(result).hasStatus(HttpStatus.CREATED);
        assertThat(result).bodyJson().extractingPath("$.message").isEqualTo("Passagier Max erfolgreich eingebucht");
    }

    @Test
    void passagierEinbuchen_wennFlugNichtVorhanden_gibt400() {
        given(buchungService.passagierEinbuchen(eq(999L), anyString(), anyInt()))
                .willThrow(new RuntimeException("Flug mit ID 999 nicht gefunden"));

        MvcTestResult result = mockMvc.post().uri("/fluege/999/buchen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Max\",\"alter\":30}")
                .exchange();
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void passagierEinbuchen_wennVoll_gibt400() {
        given(buchungService.passagierEinbuchen(eq(1L), anyString(), anyInt()))
                .willThrow(new RuntimeException("Keine freien Plaetze mehr im Flug nach Paris"));

        MvcTestResult result = mockMvc.post().uri("/fluege/1/buchen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Max\",\"alter\":30}")
                .exchange();
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void passagierAusbuchen_gibtErfolg() {
        doNothing().when(buchungService).passagierAusbuchen(eq(1L), eq("Max"), eq(30));

        MvcTestResult result = mockMvc.delete().uri("/fluege/1/ausbuchen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Max\",\"alter\":30}")
                .exchange();
        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$.message").isEqualTo("Passagier Max erfolgreich ausgebucht");
    }

    @Test
    void passagierAusbuchen_wennNichtGebucht_gibt400() {
        doThrow(new RuntimeException("Passagier Unbekannt ist in diesem Flug nicht gebucht"))
                .when(buchungService).passagierAusbuchen(eq(1L), eq("Unbekannt"), eq(99));

        MvcTestResult result = mockMvc.delete().uri("/fluege/1/ausbuchen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Unbekannt\",\"alter\":99}")
                .exchange();
        assertThat(result).hasStatus(HttpStatus.BAD_REQUEST);
    }

    @Test
    void passagiereAnzeigen_gibtListe() {
        Passagier passagier = new Passagier("Max", 30);
        given(buchungService.allePassagiere(1L)).willReturn(List.of(passagier));

        MvcTestResult result = mockMvc.get().uri("/fluege/1/passagiere").exchange();
        assertThat(result).hasStatusOk();
        assertThat(result).bodyJson().extractingPath("$[0].name").isEqualTo("Max");
        assertThat(result).bodyJson().extractingPath("$[0].alter").isEqualTo(30);
    }

    @Test
    void passagiereAnzeigen_wennFlugNichtVorhanden_gibt404() {
        given(buchungService.allePassagiere(999L))
                .willThrow(new RuntimeException("Flug mit ID 999 nicht gefunden"));

        MvcTestResult result = mockMvc.get().uri("/fluege/999/passagiere").exchange();
        assertThat(result).hasStatus(HttpStatus.NOT_FOUND);
    }
}
