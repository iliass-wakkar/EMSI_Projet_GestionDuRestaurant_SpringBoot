package com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.resolvers;

import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.annotations.CurrentUser;
import com.GestionDuRestaurant.GestionDuRestaurant_SpringBoot.modeles.UserPrincipale;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class CurrentUserResolver implements HandlerMethodArgumentResolver {



    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        // 1. Get the authentication object
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // 2. Extract your custom UserPrincipale
        UserPrincipale principal = (UserPrincipale) auth.getPrincipal();

        // 3. Return the underlying User entity
        return principal.getUser();
    }
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Return true if parameter has @CurrentUser annotation
        return parameter.hasParameterAnnotation(CurrentUser.class);
    }
}
