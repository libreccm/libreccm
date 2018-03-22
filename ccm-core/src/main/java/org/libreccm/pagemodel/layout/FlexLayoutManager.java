/*
 * Copyright (C) 2018 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.libreccm.pagemodel.layout;

import org.libreccm.pagemodel.ComponentModelRepository;

import java.io.Serializable;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FlexLayoutManager implements Serializable {

    private static final long serialVersionUID = 6697380241367469301L;

    @Inject
    private ComponentModelRepository componentModelRepo;

    @Inject
    private FlexBoxRepository flexBoxRepo;

    public void addBoxToLayout(final FlexBox box, final FlexLayout layout) {

        Objects.requireNonNull(box);
        Objects.requireNonNull(layout);

        layout.addBox(box);
        box.setLayout(layout);

        componentModelRepo.save(layout);
        flexBoxRepo.save(box);
    }

    public void removeBoxFromLayout(final FlexBox box,
                                    final FlexLayout layout) {

        Objects.requireNonNull(box);
        Objects.requireNonNull(layout);

        layout.removeBox(box);
        box.setLayout(null);

        componentModelRepo.save(layout);
        flexBoxRepo.delete(box);
    }

    public void decreaseBoxOrder(final FlexLayout layout,
                                 final FlexBox box) {
        
        Objects.requireNonNull(box);
        Objects.requireNonNull(layout);

        final int currentPosition = layout.getBoxes().indexOf(box);

        if (currentPosition < 0) {
            throw new IllegalArgumentException(String
                .format("The FlexBox with ID %d is not part "
                            + "of the FlexLayout \"%s\".",
                        box.getBoxId(),
                        layout.getUuid()));
        }
        
        final FlexBox prevBox;
        if ((currentPosition - 1) > 0) {
            prevBox = layout.getBoxes().get(currentPosition -1);
        } else {
            // No previous box, return silently.
            return;
        }
        
        final int prevPosition = prevBox.getOrder();
        
        prevBox.setOrder(currentPosition);
        box.setOrder(prevPosition);
        
        flexBoxRepo.save(box);
        flexBoxRepo.save(prevBox);
    }
    
    public void increaseBoxOrder(final FlexLayout layout,
                                 final FlexBox box) {

        Objects.requireNonNull(box);
        Objects.requireNonNull(layout);

        final int currentPosition = layout.getBoxes().indexOf(box);

        if (currentPosition < 0) {
            throw new IllegalArgumentException(String
                .format("The FlexBox with ID %d is not part "
                            + "of the FlexLayout \"%s\".",
                        box.getBoxId(),
                        layout.getUuid()));
        }
        
           final FlexBox nextBox;
        if ((currentPosition + 1) < layout.getBoxes().size()) {
            nextBox = layout.getBoxes().get(currentPosition + 1);
        } else {
            // No previous box, return silently.
            return;
        }
        
        final int nextPosition = nextBox.getOrder();
        
        nextBox.setOrder(currentPosition);
        box.setOrder(nextPosition);
        
        flexBoxRepo.save(box);
        flexBoxRepo.save(nextBox);
        
    }

}
