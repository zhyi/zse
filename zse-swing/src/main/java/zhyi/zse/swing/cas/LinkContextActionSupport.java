/*
 * Copyright (C) 2013 Zhao Yi
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package zhyi.zse.swing.cas;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import zhyi.zse.swing.Link;

/**
 * The context action support for {@link Link}.
 * <p>
 * The following actions are provided by this class:
 * <dl>
 * <dt><b>Copy Link</b>
 * <dd>Copies the link's URI to the clipboard.
 * </dl>
 *
 * @author Zhao Yi
 */
public class LinkContextActionSupport extends ContextActionSupport<Link> {
    public LinkContextActionSupport(Link link) {
        super(link);
        install(new CopyLinkAction(), 0);
    }

    @SuppressWarnings("serial")
    private class CopyLinkAction extends AbstractContextAction {
        private CopyLinkAction() {
            super("zhyi.zse.swing.cas.LinkContextActionSupport", "copyLink");
        }

        @Override
        protected void doAction() {
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
                    new StringSelection(component.getUri().toString()), null);
        }
    }
}
