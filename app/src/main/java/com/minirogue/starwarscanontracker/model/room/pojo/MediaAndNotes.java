package com.minirogue.starwarscanontracker.model.room.pojo;

import androidx.room.Embedded;

import com.minirogue.starwarscanontracker.model.room.entity.MediaItem;
import com.minirogue.starwarscanontracker.model.room.entity.MediaNotes;

public class MediaAndNotes {

    @Embedded
    public MediaItem mediaItem;
    @Embedded
    public MediaNotes mediaNotes;
}
