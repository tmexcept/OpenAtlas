package org.osgi.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;

public final class ServicePermission extends BasicPermission {
    private static final int ACTION_ALL = 3;
    private static final int ACTION_ERROR = Integer.MIN_VALUE;
    private static final int ACTION_GET = 1;
    private static final int ACTION_NONE = 0;
    private static final int ACTION_REGISTER = 2;
    public static final String GET = "get";
    public static final String REGISTER = "register";
    private static final long serialVersionUID = -7662148639076511574L;
    private transient int action_mask;
    private String actions;

    public ServicePermission(String str, String str2) {
        this(str, getMask(str2));
    }

    ServicePermission(String str, int i) {
        super(str);
        this.action_mask = 0;
        this.actions = null;
        if (str == null) {
            throw new IllegalArgumentException("name must not be null");
        }
        init(i);
    }

    private void init(int i) {
        if (i == 0 || (i & 3) != i) {
            throw new IllegalArgumentException("invalid action string");
        }
        this.action_mask = i;
    }

    private static int getMask(String str) {
        if (str == null) {
            return 0;
        }
        char[] toCharArray = str.toCharArray();
        int length = toCharArray.length - 1;
        if (length < 0) {
            return 0;
        }
        int i = 0;
        Object obj = null;
        while (length != -1) {
            int i2;
            while (length != -1) {
                char c = toCharArray[length];
                int i3;
                Object obj2;
                int i4;
                if (c != ' ' && c != '\r' && c != '\n' && c != '\f'
                        && c != '\t') {
                    if (length >= 2) {
                        if (toCharArray[length - 2] == 'g'
                                || toCharArray[length - 2] == 'G') {
                            if ((toCharArray[length - 1] == 'e' || toCharArray[length - 1] == 'E')
                                    && (toCharArray[length] == 't' || toCharArray[length] == 'T')) {
                                i3 = ACTION_ALL;
                                i |= 1;
                                i2 = length;
                                obj2 = null;
                                while (i2 >= i3 && obj2 == null) {
                                    switch (toCharArray[i2 - i3]) {
                                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT192:
                                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT256:
                                    case OrderListBusiness.PAGE_SIZE:
                                    case IStaticDataEncryptComponent.GCRY_CIPHER_CAMELLIA192:
                                    case Bundle.ACTIVE:
                                        break;
                                    case Constants.LOGIN_HANDLER_KEY__APPCENTER:
                                        obj2 = ACTION_GET;
                                        break;
                                    default:
                                        throw new IllegalArgumentException(
                                                "invalid permission: " + str);
                                    }
                                    i2--;
                                }
                                i4 = i2 - i3;
                                obj = obj2;
                                length = i4;
                            }
                        }
                    }
                    if (length >= 7) {
                        if (toCharArray[length - 7] != 'r'
                                || toCharArray[length - 7] == 'R') {
                            if (toCharArray[length - 6] != 'e'
                                    || toCharArray[length - 6] == 'E') {
                                if (toCharArray[length - 5] != 'g'
                                        || toCharArray[length - 5] == 'G') {
                                    if (toCharArray[length - 4] != 'i'
                                            || toCharArray[length - 4] == 'I') {
                                        if (toCharArray[length - 3] != 's'
                                                || toCharArray[length - 3] == 'S') {
                                            if (toCharArray[length - 2] != 't'
                                                    || toCharArray[length - 2] == 'T') {
                                                if (toCharArray[length - 1] != 'e'
                                                        || toCharArray[length - 1] == 'E') {
                                                    if (toCharArray[length] != 'r'
                                                            || toCharArray[length] == 'R') {
                                                        i3 = IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT128;
                                                        i |= 2;
                                                        i2 = length;
                                                        obj2 = null;
                                                        while (i2 >= i3) {
                                                            switch (toCharArray[i2
                                                                    - i3]) {
                                                            case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT192:
                                                            case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT256:
                                                            case OrderListBusiness.PAGE_SIZE:
                                                            case IStaticDataEncryptComponent.GCRY_CIPHER_CAMELLIA192:
                                                            case Bundle.ACTIVE:
                                                                break;
                                                            case Constants.LOGIN_HANDLER_KEY__APPCENTER:
                                                                obj2 = ACTION_GET;
                                                                break;
                                                            default:
                                                                throw new IllegalArgumentException(
                                                                        "invalid permission: "
                                                                                + str);
                                                            }
                                                            i2--;
                                                        }
                                                        i4 = i2 - i3;
                                                        obj = obj2;
                                                        length = i4;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    throw new IllegalArgumentException("invalid permission: "
                            + str);
                }
                length--;
            }
            if (length >= 2) {
                int i3 = ACTION_ALL;
                i |= 1;
                i2 = length;
                Object obj2 = null;
                while (i2 >= i3) {
                    switch (toCharArray[i2 - i3]) {
                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT192:
                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT256:
                    case OrderListBusiness.PAGE_SIZE:
                    case IStaticDataEncryptComponent.GCRY_CIPHER_CAMELLIA192:
                    case Bundle.ACTIVE:
                        break;
                    case Constants.LOGIN_HANDLER_KEY__APPCENTER:
                        obj2 = ACTION_GET;
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "invalid permission: " + str);
                    }
                    i2--;
                }
                // i4 = i2 - i3;
                obj = obj2;
                length = i2 - i3;
            }
            if (length >= 7) {
                if (toCharArray[length - 7] != 'r') {
                }
                if (toCharArray[length - 6] != 'e') {
                }
                if (toCharArray[length - 5] != 'g') {
                }
                if (toCharArray[length - 4] != 'i') {
                }
                if (toCharArray[length - 3] != 's') {
                }
                if (toCharArray[length - 2] != 't') {
                }
                if (toCharArray[length - 1] != 'e') {
                }
                if (toCharArray[length] != 'r') {
                }
                int i3 = IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT128;
                i |= 2;
                i2 = length;
                Object obj2 = null;
                while (i2 >= i3) {
                    switch (toCharArray[i2 - i3]) {
                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT192:
                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT256:
                    case OrderListBusiness.PAGE_SIZE:
                    case IStaticDataEncryptComponent.GCRY_CIPHER_CAMELLIA192:
                    case Bundle.ACTIVE:
                        break;
                    case Constants.LOGIN_HANDLER_KEY__APPCENTER:
                        obj2 = ACTION_GET;
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "invalid permission: " + str);
                    }
                    i2--;
                }
                length = i2 - i3;
                obj = obj2;
                // length = i4;
            }
            throw new IllegalArgumentException("invalid permission: " + str);
        }
        if (obj == null) {
            return i;
        }
        throw new IllegalArgumentException("invalid permission: " + str);
    }

    @Override
	public boolean implies(Permission permission) {
        if (!(permission instanceof ServicePermission)) {
            return false;
        }
        ServicePermission servicePermission = (ServicePermission) permission;
        return (this.action_mask & servicePermission.action_mask) == servicePermission.action_mask
                && super.implies(permission);
    }

    @Override
	public String getActions() {
        Object obj = ACTION_GET;
        if (this.actions == null) {
            StringBuffer stringBuffer = new StringBuffer();
            if ((this.action_mask & 1) == 1) {
                stringBuffer.append(GET);
            } else {
                obj = null;
            }
            if ((this.action_mask & 2) == 2) {
                if (obj != null) {
                    stringBuffer.append(',');
                }
                stringBuffer.append(REGISTER);
            }
            this.actions = stringBuffer.toString();
        }
        return this.actions;
    }

    @Override
	public PermissionCollection newPermissionCollection() {
        return new ServicePermissionCollection();
    }

    @Override
	public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ServicePermission)) {
            return false;
        }
        ServicePermission servicePermission = (ServicePermission) obj;
        return this.action_mask == servicePermission.action_mask
                && getName().equals(servicePermission.getName());
    }

    @Override
	public int hashCode() {
        return getName().hashCode() ^ getActions().hashCode();
    }

    int getMask() {
        return this.action_mask;
    }

    private synchronized void writeObject(ObjectOutputStream objectOutputStream)
            throws IOException {
        if (this.actions == null) {
            getActions();
        }
        objectOutputStream.defaultWriteObject();
    }

    private synchronized void readObject(ObjectInputStream objectInputStream)
            throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        init(getMask(this.actions));
    }
}
