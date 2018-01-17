<title>Backend - Architecture and Design</title>

# Package Sturcture Overview
* controllers
* models
* repositories
* exceptions
* utils


***


## Controllers
This package contains all SpringBoot controllers and handles all HTTP-Requests to the internal tomcat webserver.
* ActivityController
* LoginController
* RegistrationController
* TestController
* UserController

### ActivityController
Handles all HTTP-Requests at `/activity`. This controller is used to create, update, find and delete, the controller handles also `/activity/{id}/picture` requests, to query and update the picture of a specified activity (`id`).
### LoginController
Handles all HTTP-Requests at `/login`. This controller is used to create an authentication token, that is needed for authorized requests.
### RegistrationController
Handles all HTTP-Requests at `/register`. This controller is used to register and activate (`/register/activate`) new users, as well as requesting password reset emails (`/register/request_reset` and `/resgister/reset`).
### TestController
Handles GET-Request at `/test`. This controller is used to create a validated test user, for debugging purposes.
### UserController
Handles all HTTP-Requests at `/user`. This controller is used to query, update and upload profile pictures `/user/{id}/picture` for specified users.

***

## Models
This package contains all models that are used in the SpringBoot backend. All classes in this package contain only attributes and no programm logic, so basically all classes consist almost just of getters and setters.
* Activity
* IUAUser
* UserProfile
* Token

***

## Repositories
This package contains all interfaces to the databases of our project. The interface include rarely programm logic, that is used multiple times in the controllers to query the databases.
* ActivityRepository
* IUAUserRepository
* TokenRepository

***

## Exceptions
This package contains all exception thrown by our application. Most of the exceptions are used as a response to incorrect requests to the REST controllers. The exceptions are splitet into serveral subpackages:
* activity
* auth
* login
* registration
* storage
Each of the subpackages contain a abstract exception, from which all other expection are derived. e.g.: `InvalidPasswordException` extends `LoginException` extends `IUAExceptions`

***

## Utils
This package contains all utility classes. e.g.: `EmailClient`, `StorageService`, `TokenGenerator`.
