# Restaurant Management System API Documentation

## Base URL
```
http://localhost:8080/api
```

## Authentication
All endpoints require authentication using JWT token in the Authorization header:
```
Authorization: Bearer <token>
```

## Error Responses
All endpoints may return these common error responses:

### 401 Unauthorized
```json
{
    "error": "Unauthorized",
    "message": "Authentication required"
}
```

### 403 Forbidden
```json
{
    "error": "Access denied",
    "message": "You are not authorized to perform this action"
}
```

### 500 Internal Server Error
```json
{
    "error": "Internal Server Error",
    "message": "An unexpected error occurred"
}
```

## Endpoints

### Restaurant Management

#### Get All Restaurants
```http
GET /restaurants
```
**Access:** Owner (their restaurants), Manager (their restaurant)

**Response (200 OK)**
```json
[
    {
        "id": 1,
        "name": "Restaurant Name",
        "description": "Description",
        "address": "Address",
        "city": "City",
        "phoneNumber": "Phone",
        "email": "email@example.com",
        "manager": {
            "id": 1,
            "email": "manager@example.com",
            "fullName": "Manager Name",
            "role": "MANAGER"
        },
        "owner": {
            "id": 2,
            "email": "owner@example.com",
            "fullName": "Owner Name",
            "role": "OWNER"
        }
    }
]
```

#### Get Restaurant by ID
```http
GET /restaurants/{restaurantId}
```
**Access:** Owner (if their restaurant), Manager (if their restaurant)

**Response (200 OK)**
```json
{
    "id": 1,
    "name": "Restaurant Name",
    "description": "Description",
    "address": "Address",
    "city": "City",
    "phoneNumber": "Phone",
    "email": "email@example.com",
    "manager": {
        "id": 1,
        "email": "manager@example.com",
        "fullName": "Manager Name",
        "role": "MANAGER"
    },
    "owner": {
        "id": 2,
        "email": "owner@example.com",
        "fullName": "Owner Name",
        "role": "OWNER"
    }
}
```

#### Create Restaurant
```http
POST /restaurants
```
**Access:** Owner

**Request Body**
```json
{
    "name": "Restaurant Name",
    "description": "Description",
    "address": "Address",
    "city": "City",
    "phoneNumber": "Phone",
    "email": "email@example.com",
    "managerId": 1
}
```

**Response (201 Created)**
```json
{
    "id": 1,
    "name": "Restaurant Name",
    "description": "Description",
    "address": "Address",
    "city": "City",
    "phoneNumber": "Phone",
    "email": "email@example.com",
    "manager": {
        "id": 1,
        "email": "manager@example.com",
        "fullName": "Manager Name",
        "role": "MANAGER"
    },
    "owner": {
        "id": 2,
        "email": "owner@example.com",
        "fullName": "Owner Name",
        "role": "OWNER"
    }
}
```

#### Update Restaurant
```http
PUT /restaurants/{restaurantId}
```
**Access:** Owner (if their restaurant)

**Request Body**
```json
{
    "name": "Updated Name",
    "description": "Updated Description",
    "address": "Updated Address",
    "city": "Updated City",
    "phoneNumber": "Updated Phone",
    "email": "updated@example.com",
    "managerId": 2
}
```

**Response (200 OK)**
```json
{
    "id": 1,
    "name": "Updated Name",
    "description": "Updated Description",
    "address": "Updated Address",
    "city": "Updated City",
    "phoneNumber": "Updated Phone",
    "email": "updated@example.com",
    "manager": {
        "id": 2,
        "email": "newmanager@example.com",
        "fullName": "New Manager Name",
        "role": "MANAGER"
    },
    "owner": {
        "id": 2,
        "email": "owner@example.com",
        "fullName": "Owner Name",
        "role": "OWNER"
    }
}
```

### Menu Management

#### Get All Menus
```http
GET /menus
```
**Access:** Owner (their restaurants' menus), Manager (their restaurant's menus)

**Response (200 OK)**
```json
[
    {
        "id": 1,
        "title": "Menu Title",
        "description": "Menu Description",
        "active": true,
        "restaurant": {
            "id": 1,
            "name": "Restaurant Name"
        }
    }
]
```

#### Get Menus by Restaurant
```http
GET /restaurants/{restaurantId}/menus
```
**Access:** Owner (if their restaurant), Manager (if their restaurant)

**Response (200 OK)**
```json
[
    {
        "id": 1,
        "title": "Menu Title",
        "description": "Menu Description",
        "active": true,
        "restaurant": {
            "id": 1,
            "name": "Restaurant Name"
        }
    }
]
```

#### Get Menu by ID
```http
GET /menus/{menuId}
```
**Access:** Owner (if their restaurant's menu), Manager (if their restaurant's menu)

**Response (200 OK)**
```json
{
    "id": 1,
    "title": "Menu Title",
    "description": "Menu Description",
    "active": true,
    "restaurant": {
        "id": 1,
        "name": "Restaurant Name"
    }
}
```

#### Create Menu
```http
POST /menus
```
**Access:** Owner (for their restaurant), Manager (for their restaurant)

**Request Body**
```json
{
    "title": "Menu Title",
    "description": "Menu Description",
    "active": true,
    "restaurantId": 1
}
```

**Response (201 Created)**
```json
{
    "id": 1,
    "title": "Menu Title",
    "description": "Menu Description",
    "active": true,
    "restaurant": {
        "id": 1,
        "name": "Restaurant Name"
    }
}
```

#### Update Menu
```http
PUT /menus/{menuId}
```
**Access:** Owner (if their restaurant's menu), Manager (if their restaurant's menu)

**Request Body**
```json
{
    "title": "Updated Title",
    "description": "Updated Description",
    "active": false,
    "restaurantId": 1
}
```

**Response (200 OK)**
```json
{
    "id": 1,
    "title": "Updated Title",
    "description": "Updated Description",
    "active": false,
    "restaurant": {
        "id": 1,
        "name": "Restaurant Name"
    }
}
```

#### Delete Menu
```http
DELETE /menus/{menuId}
```
**Access:** Owner (if their restaurant's menu), Manager (if their restaurant's menu)

**Response (200 OK)**
```json
{
    "message": "Menu deleted successfully"
}
```

### Item Management

#### Get All Items
```http
GET /items
```
**Access:** Owner (items in their restaurants' menus), Manager (items in their restaurant's menus)

**Response (200 OK)**
```json
[
    {
        "id": 1,
        "name": "Item Name",
        "description": "Item Description",
        "price": 10.99,
        "category": "MAIN_COURSE",
        "available": true
    }
]
```

#### Get Items by Restaurant
```http
GET /restaurants/{restaurantId}/items
```
**Access:** Owner (if their restaurant), Manager (if their restaurant)

**Response (200 OK)**
```json
[
    {
        "id": 1,
        "name": "Item Name",
        "description": "Item Description",
        "price": 10.99,
        "category": "MAIN_COURSE",
        "available": true
    }
]
```

#### Get Items by Category
```http
GET /restaurants/{restaurantId}/items/category/{category}
```
**Access:** Owner (if their restaurant), Manager (if their restaurant)

**Parameters:**
- category: APPETIZER, MAIN_COURSE, DESSERT, BEVERAGE

**Response (200 OK)**
```json
[
    {
        "id": 1,
        "name": "Item Name",
        "description": "Item Description",
        "price": 10.99,
        "category": "MAIN_COURSE",
        "available": true
    }
]
```

#### Get Item by ID
```http
GET /items/{itemId}
```
**Access:** Owner (if item in their restaurant's menu), Manager (if item in their restaurant's menu)

**Response (200 OK)**
```json
{
    "id": 1,
    "name": "Item Name",
    "description": "Item Description",
    "price": 10.99,
    "category": "MAIN_COURSE",
    "available": true
}
```

#### Create Item
```http
POST /items
```
**Access:** Owner, Manager

**Request Body**
```json
{
    "name": "Item Name",
    "description": "Item Description",
    "price": 10.99,
    "category": "MAIN_COURSE",
    "available": true
}
```

**Response (201 Created)**
```json
{
    "id": 1,
    "name": "Item Name",
    "description": "Item Description",
    "price": 10.99,
    "category": "MAIN_COURSE",
    "available": true
}
```

#### Update Item
```http
PUT /items/{itemId}
```
**Access:** Owner (if item in their restaurant's menu), Manager (if item in their restaurant's menu)

**Request Body**
```json
{
    "name": "Updated Name",
    "description": "Updated Description",
    "price": 12.99,
    "category": "MAIN_COURSE",
    "available": false
}
```

**Response (200 OK)**
```json
{
    "id": 1,
    "name": "Updated Name",
    "description": "Updated Description",
    "price": 12.99,
    "category": "MAIN_COURSE",
    "available": false
}
```

#### Delete Item
```http
DELETE /items/{itemId}
```
**Access:** Owner (if item in their restaurant's menu), Manager (if item in their restaurant's menu)

**Response (200 OK)**
```json
{
    "message": "Item deleted successfully"
}
```

### Menu Item Management

#### Get Menu Items by Menu
```http
GET /menus/{menuId}/items
```
**Access:** Owner (if their restaurant's menu), Manager (if their restaurant's menu)

**Response (200 OK)**
```json
[
    {
        "id": 1,
        "menu": {
            "id": 1,
            "title": "Menu Title"
        },
        "item": {
            "id": 1,
            "name": "Item Name",
            "price": 10.99
        },
        "displayOrder": 1,
        "startDate": "2024-03-20",
        "endDate": "2024-12-31",
        "priceOverride": 11.99
    }
]
```

#### Get Menu Item by ID
```http
GET /menu-items/{menuItemId}
```
**Access:** Owner (if their restaurant's menu item), Manager (if their restaurant's menu item)

**Response (200 OK)**
```json
{
    "id": 1,
    "menu": {
        "id": 1,
        "title": "Menu Title"
    },
    "item": {
        "id": 1,
        "name": "Item Name",
        "price": 10.99
    },
    "displayOrder": 1,
    "startDate": "2024-03-20",
    "endDate": "2024-12-31",
    "priceOverride": 11.99
}
```

#### Add Item to Menu
```http
POST /menu-items
```
**Access:** Owner (if their restaurant's menu), Manager (if their restaurant's menu)

**Request Body**
```json
{
    "menuId": 1,
    "itemId": 1,
    "displayOrder": 1,
    "startDate": "2024-03-20",
    "endDate": "2024-12-31",
    "priceOverride": 11.99
}
```

**Response (201 Created)**
```json
{
    "id": 1,
    "menu": {
        "id": 1,
        "title": "Menu Title"
    },
    "item": {
        "id": 1,
        "name": "Item Name",
        "price": 10.99
    },
    "displayOrder": 1,
    "startDate": "2024-03-20",
    "endDate": "2024-12-31",
    "priceOverride": 11.99
}
```

#### Update Menu Item
```http
PUT /menu-items/{menuItemId}
```
**Access:** Owner (if their restaurant's menu item), Manager (if their restaurant's menu item)

**Request Body**
```json
{
    "displayOrder": 2,
    "startDate": "2024-04-01",
    "endDate": "2024-12-31",
    "priceOverride": 12.99
}
```

**Response (200 OK)**
```json
{
    "id": 1,
    "menu": {
        "id": 1,
        "title": "Menu Title"
    },
    "item": {
        "id": 1,
        "name": "Item Name",
        "price": 10.99
    },
    "displayOrder": 2,
    "startDate": "2024-04-01",
    "endDate": "2024-12-31",
    "priceOverride": 12.99
}
```

#### Remove Item from Menu
```http
DELETE /menu-items/{menuItemId}
```
**Access:** Owner (if their restaurant's menu item), Manager (if their restaurant's menu item)

**Response (200 OK)**
```json
{
    "message": "Item removed from menu successfully"
}
```

#### Remove All Items from Menu
```http
DELETE /menus/{menuId}/items
```
**Access:** Owner (if their restaurant's menu), Manager (if their restaurant's menu)

**Response (200 OK)**
```json
{
    "message": "All items removed from menu successfully"
}
```

### Reservation Management

#### Get All Reservations
```http
GET /reservations
```
**Access:** Owner (their restaurants' reservations), Manager (their restaurant's reservations)

**Response (200 OK)**
```json
[
    {
        "id": 1,
        "reservationDateTime": "2024-03-20T19:00:00",
        "numberOfGuests": 4,
        "note": "Window seat preferred",
        "restaurant": {
            "id": 1,
            "name": "Restaurant Name"
        },
        "client": {
            "id": 1,
            "fullName": "Client Name",
            "gender": "MALE",
            "ageGroup": "ADULT"
        }
    }
]
```

#### Create Reservation
```http
POST /reservations
```
**Access:** Any authenticated user

**Request Body**
```json
{
    "reservationDateTime": "2024-03-20T19:00:00",
    "numberOfGuests": 4,
    "note": "Window seat preferred",
    "restaurantId": 1,
    "gender": "MALE",
    "ageGroup": "ADULT"
}
```

**Response (201 Created)**
```json
{
    "id": 1,
    "reservationDateTime": "2024-03-20T19:00:00",
    "numberOfGuests": 4,
    "note": "Window seat preferred",
    "restaurant": {
        "id": 1,
        "name": "Restaurant Name"
    },
    "client": {
        "id": 1,
        "fullName": "Client Name",
        "gender": "MALE",
        "ageGroup": "ADULT"
    }
}
```

#### Cancel Reservation
```http
DELETE /reservations/{reservationId}
```
**Access:** Owner (if their restaurant's reservation), Manager (if their restaurant's reservation), Client (if their reservation)

**Response (200 OK)**
```json
{
    "message": "Reservation cancelled successfully"
}
``` 