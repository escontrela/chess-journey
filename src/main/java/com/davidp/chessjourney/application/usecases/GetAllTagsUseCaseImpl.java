package com.davidp.chessjourney.application.usecases;

import com.davidp.chessjourney.domain.common.Tag;
import com.davidp.chessjourney.domain.common.TagRepository;

import java.util.List;

public class GetAllTagsUseCaseImpl implements GetAllTagsUseCase {

  private final TagRepository tagRepository;


  public GetAllTagsUseCaseImpl(TagRepository tagRepository) {

    this.tagRepository = tagRepository;
  }

  @Override
  public List<Tag> execute() {

    return tagRepository.getAll();
  }
}
