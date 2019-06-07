package com.minirogue.starwarscanontracker.database;

import androidx.room.Embedded;

public class MediaAndNotes {

    @Embedded
    public MediaItem mediaItem;
    @Embedded
    public MediaNotes mediaNotes;
}
