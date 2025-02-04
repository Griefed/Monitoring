/* MIT License
 *
 * Copyright (c) 2021 SUK-IT
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.griefed.monitoring.controllers;

import de.griefed.monitoring.utilities.UpdateChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * RestController for getting information regarding available updates.
 * @author Griefed
 */
@RestController
@CrossOrigin(origins = {"*"})
@RequestMapping("/api/v1/updates")
public class UpdateCheckerController {

    private final UpdateChecker UPDATECHECKER;

    @Autowired
    public UpdateCheckerController(UpdateChecker injectedUpdateChecker) {
        this.UPDATECHECKER = injectedUpdateChecker;
    }

    @GetMapping(produces = "application/json")
    public String getUpdateInformation() {

        if (UPDATECHECKER.getUpdater().contains(";")) {

            return "{" +
                    "\"available\": true," +
                    "\"version\": \"" + UPDATECHECKER.getUpdater().split(";")[0] + "\"," +
                    "\"link\": \"" + UPDATECHECKER.getUpdater().split(";")[1] + "\"" +
                    "}";
        } else {

            return "{" +
                    "\"available\": false" +
                    "}";
        }
    }
}
