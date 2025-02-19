package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.common.Tag;

import java.util.List;


public interface GetAllTagsUseCase {

  List<Tag> execute();
}
