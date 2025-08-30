package co.com.crediya.api.mapper;

import co.com.crediya.api.dto.CreateUserDTO;
import co.com.crediya.api.dto.UserDTO;
import co.com.crediya.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperDTOTest {

    @Autowired
    private UserMapperDTO mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserMapperDTO.class);
    }

    @Test
    void testToModel() {
        CreateUserDTO dto = new CreateUserDTO();
        dto.setFirstName("Juan");
        dto.setLastName("Perez");
        dto.setEmail("juan.perez@mail.com");
        dto.setIdentityDocument("12345678");

        User user = mapper.toModel(dto);

        assertNotNull(user);
        assertEquals("Juan", user.getFirstName());
        assertEquals("Perez", user.getLastName());
        assertEquals("juan.perez@mail.com", user.getEmail());
        assertEquals("12345678", user.getIdentityDocument());
    }

    @Test
    void testToResponse() {
        User user = new User();
        user.setFirstName("Maria");
        user.setLastName("Gomez");
        user.setEmail("maria.gomez@mail.com");
        user.setIdentityDocument("87654321");

        UserDTO dto = mapper.toResponse(user);

        assertNotNull(dto);
        assertEquals("Maria", dto.getFirstName());
        assertEquals("Gomez", dto.getLastName());
        assertEquals("maria.gomez@mail.com", dto.getEmail());
        assertEquals("87654321", dto.getIdentityDocument());
    }
}
