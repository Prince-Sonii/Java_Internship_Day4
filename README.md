This is a console-based Java application that allows users to create, view, search, and delete personal notes — securely. Each user has a separate notes file, and access is granted only after successful authentication.

💡 Features Implemented
1. User Registration & Login
Users can register with a unique username and password.

Login requires matching credentials stored in users.txt.

Passwords are stored in plain text (for learning/demo purposes).

Only authenticated users can access their notes.

2. Per-User Note Storage
Each user has a separate notes file: notes_<username>.txt.

Notes are stored with a timestamp and a standard separator (--- END NOTE ---) for clarity and parsing.

3. Core Functionalities
Once logged in, the user can:

Create Note: Add a new note, saved along with a timestamp.

View All Notes: Read and display all the notes of the logged-in user.

Search Notes: Look up specific notes using a keyword.

Delete Notes: Remove notes that match a given keyword.

Logout: Exit the session and return to the main menu.

