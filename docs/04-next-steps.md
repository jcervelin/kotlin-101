# Next Steps

The project is intentionally small. The next useful increments are:

1. Add a database adapter behind `OrderRepository`.
2. Add request validation before `PlaceOrderRequest.toCommand()`.
3. Add Ktor authentication with the `Authentication` plugin.
4. Change repository methods to `suspend fun` and discuss coroutine boundaries.
5. Add pagination to `GET /orders`.
6. Add a rejected-order test.
7. Replace manual wiring with Koin or another DI option and compare the result.

The core boundary should stay the same:

```text
HTTP DTO -> command -> service -> domain -> response DTO
```
