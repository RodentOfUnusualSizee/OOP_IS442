// Comments :
// GET user
endpoint1 = {
    "/localhost:8080/api/user/get/{id}": {
        "request type": "GET",
        "200response": {
            "id": 2,
            "email": "gay@example.com",
            "password": "password123",
            "firstName": "Caleb",
            "lastName": "Gay",
            "role": "user",
            "activity": null,
            "portfolios": []
        }
    }
}

//Create new user
endpoint2= {
    "/localhost:8080/api/user/create": {
        "request type": "POST",
        "request body": {
            "email": "gay@example.com",
            "password": "password123",
            "firstName": "Caleb",
            "lastName": "Gay",
            "role": "user"
        },
        "200response": {
            "id": 2,
            "email": "gay@example.com",
            "password": "password123",
            "firstName": "Caleb",
            "lastName": "Gay",
            "role": "user",
            "activity": null,
            "portfolios": []
        }
    }
}