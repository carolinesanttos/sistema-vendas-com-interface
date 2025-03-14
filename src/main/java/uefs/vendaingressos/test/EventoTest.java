package uefs.vendaingressos.test;

import java.util.Date;
import java.util.List;
import java.util.Calendar;

import org.junit.Test;
import uefs.vendaingressos.model.Evento;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class EventoTest {

    @Test
    public void testCriarEvento() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.SEPTEMBER, 10);
        Date data = calendar.getTime();

        Evento evento = new Evento("Show de Rock", "Banda XYZ", data);

        assertNotNull(evento);
        assertEquals("Show de Rock", evento.getNome());
        assertEquals("Banda XYZ", evento.getDescricao());
        assertEquals(data, evento.getData());
    }

    @Test
    public void testAdicionarAssento() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.SEPTEMBER, 10);
        Date data = calendar.getTime();

        Evento evento = new Evento("Show de Rock", "Banda XYZ", data);
        evento.adicionarAssento("A1");

        List<String> assentos = evento.getAssentosDisponiveis();
        assertTrue(assentos.contains("A1"));
    }

    @Test
    public void testRemoverAssento() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.SEPTEMBER, 10);
        Date data = calendar.getTime();

        Evento evento = new Evento("Show de Rock", "Banda XYZ", data);
        evento.adicionarAssento("A1");
        evento.removerAssentoDisponivel("A1");

        List<String> assentos = evento.getAssentosDisponiveis();
        assertFalse(assentos.contains("A1"));
    }

    @Test
    public void testEventoAtivo() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2024, Calendar.NOVEMBER, 30); // Foi necessário alterar a data
        Date data = calendar.getTime();

        Evento evento = new Evento("Show de Rock", "Banda XYZ", data);

        assertTrue(evento.isAtivo());
    }

    @Test
    public void testEventoInativo() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2023, Calendar.JANUARY, 10);
        Date data = calendar.getTime();

        Evento evento = new Evento("Show de Rock", "Banda XYZ", data);

        assertFalse(evento.isAtivo());
    }
}
