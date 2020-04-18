/*
 * This file is part of cnesreport.
 *
 * cnesreport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * cnesreport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with cnesreport.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.cnes.sonar.report.providers;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.cnes.sonar.report.exceptions.BadSonarQubeRequestException;
import fr.cnes.sonar.report.exceptions.SonarQubeException;
import fr.cnes.sonar.report.model.Language;
import fr.cnes.sonar.report.model.SonarQubeServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides all project keys
 */
public class AllProjectProvider extends AbstractDataProvider {

    private final List<String> projects = new ArrayList<>();


    /**
     * Complete constructor.
     *
     * @param pServer SonarQube server.
     * @param pToken  String representing the user token.
     */
    public AllProjectProvider(final SonarQubeServer pServer, final String pToken) {
        super(pServer, pToken, null);
    }


    /**
     * Get all Projects
     *
     * @throws BadSonarQubeRequestException when the server does not understand the request
     * @throws SonarQubeException           When SonarQube server is not callable.
     */
    public List<String> getProjects() throws BadSonarQubeRequestException, SonarQubeException {
        // send a request to sonarqube server and return th response as a json object
        // if there is an error on server side this method throws an exception

        // TODO only works for project count <= 500
        final JsonObject jo = request(String.format(getRequest(GET_ALL_PROJECTS_REQUEST), getServer().getUrl()));
        final JsonArray components = jo.get("components").getAsJsonArray();
        components.forEach(component -> {
            this.projects.add(component.getAsJsonObject().get("key").getAsString());
        });
        return this.projects;
    }
}
