/*
 * Copyright (c) 2014 Oliver Seemann
 *
 * This file is part of Jalos.
 *
 * Jalos is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Jalos is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Jalos.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.oebs.jalos;

import net.oebs.jalos.db.Backend;

public class RuntimeContext {

    private static RuntimeContext instance = null;

    private Backend db;
    private Settings settings;

    public RuntimeContext() {
    }

    public static synchronized RuntimeContext getInstance() {
        if (RuntimeContext.instance == null) {
            RuntimeContext.instance = new RuntimeContext();
        }
        return RuntimeContext.instance;
    }

    public void setBackend(Backend db) {
        this.db = db;
    }

    public Backend getBackend() {
        return this.db;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
    }

    public Settings getSettings() {
        return this.settings;
    }
}
