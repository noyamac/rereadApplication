package com.colman.reread.model

object UserRepository {
    var currentUser: User = User(
        id = "1",
        name = "John Doe",
        email = "john.doe@example.com",
        phone = "+1 234 567 890",
        country = "United States",
        city = "New York",
        profileImageUrl = ""
    )
}
