package com.example.demo.common

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Todo not found")
class TodoNotFoundException(id: Int) : Exception("could not found todo with id $id")