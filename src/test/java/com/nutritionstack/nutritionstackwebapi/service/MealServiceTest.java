package com.nutritionstack.nutritionstackwebapi.service;

import com.nutritionstack.nutritionstackwebapi.dto.meal.*;
import com.nutritionstack.nutritionstackwebapi.model.meal.*;
import com.nutritionstack.nutritionstackwebapi.model.nutrition.*;
import com.nutritionstack.nutritionstackwebapi.repository.meal.*;
import com.nutritionstack.nutritionstackwebapi.repository.tracking.*;
import com.nutritionstack.nutritionstackwebapi.service.meal.MealDtoConverterService;
import com.nutritionstack.nutritionstackwebapi.service.meal.MealService;
import com.nutritionstack.nutritionstackwebapi.service.meal.MealValidationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MealServiceTest {

    @Mock
    private MealRepository mealRepository;

    @Mock
    private MealProductRepository mealProductRepository;

    @Mock
    private LoggedProductRepository loggedProductRepository;

    @Mock
    private MealValidationService mealValidationService;

    @Mock
    private MealDtoConverterService mealDtoConverterService;

    @InjectMocks
    private MealService mealService;

    private MealCreateRequestDTO validRequest;
    private Meal validMeal;
    private MealProduct validMealProduct;
    private MealResponseDTO expectedResponse;

    @BeforeEach
    void setUp() {
        validRequest = new MealCreateRequestDTO();
        validRequest.setMealName("Test Meal");
        validRequest.setMealType(MealType.BREAKFAST);
        validRequest.setProducts(Arrays.asList(
            new MealProductDTO("1234567890123", 100.0, Unit.G)
        ));

        validMeal = new Meal("Test Meal", MealType.BREAKFAST, 1L);
        validMeal.setId(1L);
        validMeal.setCreatedAt(LocalDateTime.now());

        validMealProduct = new MealProduct(validMeal, "1234567890123", 100.0, Unit.G);
        validMealProduct.setId(1L);
        validMeal.setMealProducts(Arrays.asList(validMealProduct));

        expectedResponse = new MealResponseDTO(
            1L, "Test Meal", MealType.BREAKFAST, 1L, 
            LocalDateTime.now(), null, Arrays.asList(), null
        );
    }

    @Test
    void createMeal_WithValidData_ShouldCreateMeal() {
        when(mealRepository.save(any(Meal.class))).thenAnswer(invocation -> {
            Meal meal = invocation.getArgument(0);
            meal.setId(1L);
            return meal;
        });
        when(mealDtoConverterService.convertToResponseDTO(any(Meal.class)))
            .thenReturn(expectedResponse);

        MealResponseDTO result = mealService.createMeal(validRequest, 1L);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(mealValidationService).validateUserExists(1L);
        verify(mealValidationService).validateProductsExist(validRequest.getProducts());
        verify(mealRepository, times(2)).save(any(Meal.class));
    }

    @Test
    void getMealById_WithValidId_ShouldReturnMeal() {
        when(mealValidationService.getMealEntityByIdAndUser(1L, 1L, false))
            .thenReturn(validMeal);
        when(mealDtoConverterService.convertToResponseDTO(validMeal))
            .thenReturn(expectedResponse);

        MealResponseDTO result = mealService.getMealById(1L, 1L, false);

        assertNotNull(result);
        assertEquals(expectedResponse, result);
        verify(mealValidationService).getMealEntityByIdAndUser(1L, 1L, false);
        verify(mealDtoConverterService).convertToResponseDTO(validMeal);
    }

    @Test
    void deleteMeal_WithValidId_ShouldDeleteMeal() {
        when(mealValidationService.getMealEntityByIdAndUser(1L, 1L, false))
            .thenReturn(validMeal);

        mealService.deleteMeal(1L, 1L, false);

        verify(mealValidationService).getMealEntityByIdAndUser(1L, 1L, false);
        verify(mealRepository).delete(validMeal);
    }
}
