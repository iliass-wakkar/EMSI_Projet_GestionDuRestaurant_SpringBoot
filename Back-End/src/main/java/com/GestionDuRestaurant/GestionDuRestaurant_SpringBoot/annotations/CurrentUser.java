package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.annotations;
import java.lang.annotation.*;

@Target(ElementType.PARAMETER) // Only works on method parameters
@Retention(RetentionPolicy.RUNTIME) // Available during runtime
public @interface CurrentUser {}
