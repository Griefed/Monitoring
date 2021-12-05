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
package de.griefed.monitoring.components;

import de.griefed.monitoring.models.InformationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.software.os.OSFileStore;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Class responsible for retrieving all interesting values about the disk drives.
 * @author Griefed
 */
@Service
public class DiskComponent implements InformationModel {

    private final SystemInfo SYSTEM_INFO = new SystemInfo();
    private final List<OSFileStore> DISK_STORES = SYSTEM_INFO.getOperatingSystem().getFileSystem().getFileStores(true);
    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

    private List<HashMap<String, String>> diskInformationList = new ArrayList<>();
    private String diskInformation;

    /**
     * Constructor responsible for DI.
     * @author Griefed
     */
    @Autowired
    public DiskComponent() {
        updateValues();
    }

    @Override
    public void sendNotification() {

    }

    @Override
    public void setValues() {
        if (diskInformationList.isEmpty()) {
            updateValues();
        }

        StringBuilder stringBuilder = new StringBuilder();

        if (diskInformationList.size() > 1) {

            stringBuilder.append("{");
            stringBuilder.append("\"name\": \"").append(diskInformationList.get(0).get("name")).append("\",");
            stringBuilder.append("\"size\": \"").append(diskInformationList.get(0).get("size")).append("\",");
            stringBuilder.append("\"free\": \"").append(diskInformationList.get(0).get("free")).append("\",");
            stringBuilder.append("\"used\": \"").append(diskInformationList.get(0).get("used")).append("\"");
            stringBuilder.append("},");

            for (int i = 1; i < DISK_STORES.size() -1; i++) {

                stringBuilder.append("{");
                stringBuilder.append("\"name\": \"").append(diskInformationList.get(i).get("name")).append("\",");
                stringBuilder.append("\"size\": \"").append(diskInformationList.get(i).get("size")).append("\",");
                stringBuilder.append("\"free\": \"").append(diskInformationList.get(i).get("free")).append("\",");
                stringBuilder.append("\"used\": \"").append(diskInformationList.get(i).get("used")).append("\"");
                stringBuilder.append("},");
            }

            stringBuilder.append("{");
            stringBuilder.append("\"name\": \"").append(diskInformationList.get(diskInformationList.size() - 1).get("name")).append("\",");
            stringBuilder.append("\"size\": \"").append(diskInformationList.get(diskInformationList.size() - 1).get("size")).append("\",");
            stringBuilder.append("\"free\": \"").append(diskInformationList.get(diskInformationList.size() - 1).get("free")).append("\",");
            stringBuilder.append("\"used\": \"").append(diskInformationList.get(diskInformationList.size() - 1).get("used")).append("\"");
            stringBuilder.append("}");

        } else {

            stringBuilder.append("{");
            stringBuilder.append("\"name\": \"").append(diskInformationList.get(0).get("name")).append("\",");
            stringBuilder.append("\"size\": \"").append(diskInformationList.get(0).get("size")).append("\",");
            stringBuilder.append("\"free\": \"").append(diskInformationList.get(0).get("free")).append("\",");
            stringBuilder.append("\"used\": \"").append(diskInformationList.get(0).get("used")).append("\"");
            stringBuilder.append("},");

        }

        this.diskInformation = stringBuilder.toString();
    }

    @Override
    public void updateValues() {
        List<HashMap<String, String>> list = new ArrayList<>();

        if (DISK_STORES.size() > 1) {

            list.add(
                    new HashMap<String, String>() {
                {
                    put("name", DISK_STORES.get(0).getName() + " " + DISK_STORES.get(0).getLabel());
                    put("size", (DISK_STORES.get(0).getTotalSpace() / 1073741824) + " GB");
                    put("free", (DISK_STORES.get(0).getFreeSpace() / 1073741824) + " GB");
                    put("used", DECIMAL_FORMAT.format(100 - ((100F / DISK_STORES.get(0).getTotalSpace()) * DISK_STORES.get(0).getFreeSpace())) + " %");
                }
            });

            for (int i = 1; i < DISK_STORES.size() -1; i++) {

                int finalI = i;
                list.add(
                        new HashMap<String, String>() {
                            {
                                put("name", DISK_STORES.get(finalI).getName() + " " + DISK_STORES.get(finalI).getLabel());
                                put("size", (DISK_STORES.get(finalI).getTotalSpace() / 1073741824) + " GB");
                                put("free", (DISK_STORES.get(finalI).getFreeSpace() / 1073741824) + " GB");
                                put("used", DECIMAL_FORMAT.format(100 - ((100F / DISK_STORES.get(finalI).getTotalSpace()) * DISK_STORES.get(finalI).getFreeSpace())) + " %");
                            }
                        });

            }

            list.add(
                    new HashMap<String, String>() {
                        {
                            put("name", DISK_STORES.get(DISK_STORES.size() - 1).getName() + " " + DISK_STORES.get(DISK_STORES.size() - 1).getLabel());
                            put("size", (DISK_STORES.get(DISK_STORES.size() - 1).getTotalSpace() / 1073741824) + " GB");
                            put("free", (DISK_STORES.get(DISK_STORES.size() - 1).getFreeSpace() / 1073741824) + " GB");
                            put("used", DECIMAL_FORMAT.format(100 - ((100F / DISK_STORES.get(DISK_STORES.size() - 1).getTotalSpace()) * DISK_STORES.get(DISK_STORES.size() - 1).getFreeSpace())) + " %");
                        }
                    });


        } else {

            list.add(
                    new HashMap<String, String>() {
                        {
                            put("name", DISK_STORES.get(0).getName() + " " + DISK_STORES.get(0).getLabel());
                            put("size", (DISK_STORES.get(0).getTotalSpace() / 1073741824) + " GB");
                            put("free", (DISK_STORES.get(0).getFreeSpace() / 1073741824) + " GB");
                            put("used", DECIMAL_FORMAT.format(100 - ((100F / DISK_STORES.get(0).getTotalSpace()) * DISK_STORES.get(0).getFreeSpace())) + " %");
                        }
                    });

        }

        diskInformationList = list;
    }

    /**
     * Getter for the name of this component.
     * @author Griefed
     * @return String. Returns the name of the component.
     */
    @Override
    public String getName() {
        return "disks";
    }

    /**
     * Getter for the information about the disk drives. Gathers information, for each disk, about the name, total space
     * and free space left.
     * @author Griefed
     * @return String. Information about the disk drives in JSON format.
     */
    @Override
    public String getValues() {
        if (diskInformation == null) {
            setValues();
        }

        return diskInformation;
    }

    @Override
    public String toString() {
        return "\"" + getName() + "\": [" + getValues() + "]";
    }
}
