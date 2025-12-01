package com.example.employeemanagementsystem.controller;

import com.example.employeemanagementsystem.dto.EmployeeDTO;
import com.example.employeemanagementsystem.enums.Grade;
import com.example.employeemanagementsystem.service.EmployeeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private EmployeeDTO employeeDTO;

    @BeforeEach
    void setUp() {
        employeeDTO = new EmployeeDTO();
        employeeDTO.setEmployeeId("0001");
        employeeDTO.setName("John Doe");
        employeeDTO.setGrade(Grade.GRADE_6);
        employeeDTO.setAddress("123 Test St");
        employeeDTO.setMobileNumber("1234567890");
    }

    @Test
    void testCreateEmployee_Success() throws Exception {
        when(employeeService.createEmployee(any(EmployeeDTO.class))).thenReturn(employeeDTO);

        mockMvc.perform(post("/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeId").value("0001"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(employeeService, times(1)).createEmployee(any(EmployeeDTO.class));
    }

    @Test
    void testUpdateEmployee_Success() throws Exception {
        when(employeeService.updateEmployee(eq("0001"), any(EmployeeDTO.class))).thenReturn(employeeDTO);

        mockMvc.perform(put("/employees/0001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(employeeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("0001"));

        verify(employeeService, times(1)).updateEmployee(eq("0001"), any(EmployeeDTO.class));
    }

    @Test
    void testGetEmployee_Success() throws Exception {
        when(employeeService.getEmployee("0001")).thenReturn(employeeDTO);

        mockMvc.perform(get("/employees/0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.employeeId").value("0001"))
                .andExpect(jsonPath("$.name").value("John Doe"));

        verify(employeeService, times(1)).getEmployee("0001");
    }

    @Test
    void testGetAllEmployees_Success() throws Exception {
        List<EmployeeDTO> employees = Arrays.asList(employeeDTO);
        when(employeeService.getAllEmployees()).thenReturn(employees);

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].employeeId").value("0001"));

        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    void testDeleteEmployee_Success() throws Exception {
        doNothing().when(employeeService).deleteEmployee("0001");

        mockMvc.perform(delete("/employees/0001"))
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee("0001");
    }

    @Test
    void testGetEmployeesByGrade_Success() throws Exception {
        List<EmployeeDTO> employees = Arrays.asList(employeeDTO);
        when(employeeService.getEmployeesByGrade(Grade.GRADE_6)).thenReturn(employees);

        mockMvc.perform(get("/employees/grade/GRADE_6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());

        verify(employeeService, times(1)).getEmployeesByGrade(Grade.GRADE_6);
    }

    @Test
    void testCanAddEmployeeToGrade_Success() throws Exception {
        when(employeeService.canAddEmployeeToGrade(Grade.GRADE_6)).thenReturn(true);

        mockMvc.perform(get("/employees/grade/GRADE_6/can-add"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));

        verify(employeeService, times(1)).canAddEmployeeToGrade(Grade.GRADE_6);
    }
}

