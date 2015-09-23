/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.bebop.util;

/**
 * <p>A class for Bebop color parameters.</p>
 *
 * <blockquote><pre>
 * private Page buildSomePage() {
 *     Page page = new Page("Some Page");
 *
 *     Label label = new Label("Some Text");
 *
 *     // Make the label text green.
 *     label.setColor(new Color(0,255,0));
 *
 *     // Here's another way of doing the same thing.
 *     label.setColor(Color.green);
 *
 *     page.add(label);
 *
 *     page.lock();
 *
 *     return page;
 * }
 * </pre></blockquote>
 *
 * @author Jim Parsons
 * @version $Id: Color.java 287 2005-02-22 00:29:02Z sskracic $
 * @deprecated without replacement. Bebop must never directly specify design
 *             properties but semantic properties. tjhe theme engine
 *             decides about the design and the display!
 */
public class Color {

    private int m_red = 255;
    private int m_green = 255;
    private int m_blue = 255;

    /** An instance of the color black. */
    public static final Color black = new Color(0,0,0);

    /** An instance of the color blue. */
    public static final Color blue = new Color(0,0,255);

    /** An instance of the color cyan. */
    public static final Color cyan = new Color(0,255,255);

    /** An instance of the color darkGray. */
    public static final Color darkGray = new Color(169,169,169);

    /** An instance of the color gray. */
    public static final Color gray = new Color(128,128,128);

    /** An instance of the color green. */
    public static final Color green = new Color(0,128,0);

    /** An instance of the color lightGray. */
    public static final Color lightGray = new Color(211,211,211);

    /** An instance of the color magenta. */
    public static final Color magenta = new Color(255,0,255);

    /** An instance of the color orange. */
    public static final Color orange = new Color(255,165,0);

    /** An instance of the color pink. */
    public static final Color pink = new Color(255,192,203);

    /** An instance of the color red. */
    public static final Color red = new Color(255,0,0);

    /** An instance of the color white. */
    public static final Color white = new Color(255,255,255);

    /** An instance of the color yellow. */
    public static final Color yellow = new Color(255,255,0);

    /** An instance of the color nobukoBlue, an exotic medium torquoise. */
    public static final Color nobukoBlue = new Color(72,209,204);

    /**
     * Make a color from the constituents red, green, and blue. Each
     * color argument is an integer in the range of 0 to 255.
     *
     * @param red the amount of red.  Must be an int in the range of 0 to 255.
     * @param green the amount of green.  Must be an int in the range of 0 to
     * 255.
     * @param blue the amount of blue.  Must be an int in the range of 0 to 255.
     * @deprecated without replacement. Bebop must never directly specify design
     *             properties but semantic properties. tjhe theme engine
     *             decides about the design and the display!
     */
    public Color(int redValue, int greenValue, int blueValue) {
        if (redValue >= 0 && redValue < 256) {
            m_red = redValue;
        }

        if (greenValue >= 0 && greenValue < 256) {
            m_green = greenValue;
        }

        if (blueValue >= 0 && blueValue < 256) {
            m_blue = blueValue;
        }
    }

    /**
     * Make a color from the constituents red, green, and blue. Each
     * color argument is a float in the range of 0 to 1.
     *
     * @param red the amount of red.  Must be a float in the range of 0 to 1.
     * @param green the amount of green.  Must be a float in the range of 0 to
     * 1.
     * @param blue the amount of blue.  Must be a float in the range of 0 to 1.
     */
    public Color(float redValue, float greenValue, float blueValue) {
        if (redValue >= 0.0f && redValue <= 1.0f) {
            m_red = (int)(255 * redValue);
        }

        if (greenValue >= 0.0f && greenValue <= 1.0f) {
            m_green = (int)(255 * greenValue);
        }

        if(blueValue >= 0.0f && blueValue <= 1.0f) {
            m_blue = (int)(255 * blueValue);
        }
    }

    /**
     * Return a string with hex values padded out two places with a
     * leading 0.
     *
     * @return a string representing this color.
     */
    public String toString() {
        String result, redString, greenString, blueString;

        if (m_red < 16) {
            redString = "0" + Integer.toHexString(m_red);
        } else {
            redString = Integer.toHexString(m_red);
        }

        if (m_green < 16) {
            greenString = "0" + Integer.toHexString(m_green);
        } else {
            greenString = Integer.toHexString(m_green);
        }

        if (m_blue < 16) {
            blueString = "0" + Integer.toHexString(m_blue);
        } else {
            blueString = Integer.toHexString(m_blue);
        }

        result = redString + greenString + blueString;

        return result;
    }

    /**
     * Produce an HTML hex-based representation of this color.
     *
     * @return an HTML hex color.
     */
    public String toHTMLString() {
        String result = "#" + this.toString();

        return result;
    }
}
