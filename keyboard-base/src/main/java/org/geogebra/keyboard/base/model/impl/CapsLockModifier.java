package org.geogebra.keyboard.base.model.impl;

import java.util.HashMap;

import org.geogebra.keyboard.base.Action;
import org.geogebra.keyboard.base.ActionType;
import org.geogebra.keyboard.base.Background;
import org.geogebra.keyboard.base.Resource;
import org.geogebra.keyboard.base.ResourceType;
import org.geogebra.keyboard.base.model.KeyModifier;

public class CapsLockModifier implements KeyModifier {

    private boolean capsLock = false;
    private HashMap<String, String> upperKeys;

    public void toggleCapsLock() {
        capsLock = !capsLock;
    }

    public boolean disableCapsLock() {
        boolean capsLockWasEnabled = capsLock;
        capsLock = false;
        return capsLockWasEnabled;
    }

    @Override
    public String modifyResourceName(String resourceName, ResourceType resourceType) {
        if (resourceType == ResourceType.TEXT && resourceName.length() == 1) {
            if (capsLock) {
                return resourceName.toUpperCase();
            }
        } else if (resourceType == ResourceType.DEFINED_CONSTANT && resourceName.equals(Resource.CAPS_LOCK.name())) {
            if (capsLock) {
                return Resource.CAPS_LOCK_ENABLED.name();
            }
        }
        return resourceName;
    }

	private String getUpperCase(String resourceName) {
		return upperKeys.get(resourceName);
	}

	@Override
    public String modifyActionName(String actionName, ActionType actionType) {
        if (actionType == ActionType.INPUT && actionName.length() == 1) {
            if (capsLock) {
				//if (upperKeys != null) {
				//	return getUpperCase(actionName);
				//}
				return actionName.toUpperCase();
            }
        }
        return actionName;
    }

    @Override
    public Background modifyBackground(Background background, ActionType actionType, String actionName) {
        if (actionType == ActionType.CUSTOM && actionName.equals(Action.CAPS_LOCK.name())) {
            if (capsLock) {
                return Background.FUNCTIONAL_PRESSED;
            }
        }
        return background;
    }

	public void setUpperKeys(HashMap<String, String> uKeys) {
		upperKeys = uKeys;
	}
}
