Database Design Planning Page

Potential Tables:

- User (id, username, password, name, namePublic, roleid1, roleid2)
        int, varchar, varchar, varchar, boolean, int, int

- Role (id, roleName)
        int, varchar

Current Queue --

Public Artist Playlist (id, artistid, playlistid)
        playlistid references a whole new table were theres one playlist that contains (id, )