package org.osgi.framework;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.BasicPermission;
import java.security.Permission;
import java.security.PermissionCollection;

public final class PackagePermission extends BasicPermission {
    private static final int ACTION_ALL = 3;
    private static final int ACTION_ERROR = Integer.MIN_VALUE;
    private static final int ACTION_EXPORT = 1;
    private static final int ACTION_IMPORT = 2;
    private static final int ACTION_NONE = 0;
    public static final String EXPORT = "export";
    public static final String IMPORT = "import";
    private static final long serialVersionUID = -5107705877071099135L;
    private transient int action_mask;
    private String actions;

    public PackagePermission(String str, String str2) {
        this(str, getMask(str2));
    }

    PackagePermission(String str, int i) {
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
                Object obj2;
                int i3;
                if (c != ' ' && c != '\r' && c != '\n' && c != '\f'
                        && c != '\t') {
                    if (length >= 5) {
                        if (toCharArray[length - 5] == 'i'
                                || toCharArray[length - 5] == 'I') {
                            if (toCharArray[length - 4] == 'm'
                                    || toCharArray[length - 4] == 'M') {
                                if (toCharArray[length - 3] == 'p'
                                        || toCharArray[length - 3] == 'P') {
                                    if (toCharArray[length - 2] == 'o'
                                            || toCharArray[length - 2] == 'O') {
                                        if ((toCharArray[length - 1] == 'r' || toCharArray[length - 1] == 'R')
                                                && (toCharArray[length] == 't' || toCharArray[length] == 'T')) {
                                            i |= 2;
                                            i2 = length;
                                            obj2 = null;
                                            while (i2 >= 6 && obj2 == null) {
                                                switch (toCharArray[i2 - 6]) {
                                                case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT192:
                                                case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT256:
                                                case OrderListBusiness.PAGE_SIZE:
                                                case IStaticDataEncryptComponent.GCRY_CIPHER_CAMELLIA192:
                                                case Bundle.ACTIVE:
                                                    break;
                                                case Constants.LOGIN_HANDLER_KEY__APPCENTER:
                                                    obj2 = ACTION_EXPORT;
                                                    break;
                                                default:
                                                    throw new IllegalArgumentException(
                                                            "invalid permission: "
                                                                    + str);
                                                }
                                                i2--;
                                            }
                                            i3 = i2 - 6;
                                            obj = obj2;
                                            length = i3;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    if (length >= 5) {
                        if (toCharArray[length - 5] != 'e'
                                || toCharArray[length - 5] == 'E') {
                            if (toCharArray[length - 4] != 'x'
                                    || toCharArray[length - 4] == 'X') {
                                if (toCharArray[length - 3] != 'p'
                                        || toCharArray[length - 3] == 'P') {
                                    if (toCharArray[length - 2] != 'o'
                                            || toCharArray[length - 2] == 'O') {
                                        if (toCharArray[length - 1] != 'r'
                                                || toCharArray[length - 1] == 'R') {
                                            if (toCharArray[length] != 't'
                                                    || toCharArray[length] == 'T') {
                                                i |= 3;
                                                i2 = length;
                                                obj2 = null;
                                                while (i2 >= 6) {
                                                    switch (toCharArray[i2 - 6]) {
                                                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT192:
                                                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT256:
                                                    case OrderListBusiness.PAGE_SIZE:
                                                    case IStaticDataEncryptComponent.GCRY_CIPHER_CAMELLIA192:
                                                    case Bundle.ACTIVE:
                                                        break;
                                                    case Constants.LOGIN_HANDLER_KEY__APPCENTER:
                                                        obj2 = ACTION_EXPORT;
                                                        break;
                                                    default:
                                                        throw new IllegalArgumentException(
                                                                "invalid permission: "
                                                                        + str);
                                                    }
                                                    i2--;
                                                }
                                                i3 = i2 - 6;
                                                obj = obj2;
                                                length = i3;
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
            if (length >= 5) {
                i |= 2;
                i2 = length;

                while (i2 >= 6) {
                    switch (toCharArray[i2 - 6]) {
                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT192:
                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT256:
                    case OrderListBusiness.PAGE_SIZE:
                    case IStaticDataEncryptComponent.GCRY_CIPHER_CAMELLIA192:
                    case Bundle.ACTIVE:
                        break;
                    case Constants.LOGIN_HANDLER_KEY__APPCENTER:
                        obj = ACTION_EXPORT;
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "invalid permission: " + str);
                    }
                    i2--;
                }
                length = i2 - 6;
                // obj = obj2;
                // length = i3;
            }
            if (length >= 5) {
                if (toCharArray[length - 5] != 'e') {
                }
                if (toCharArray[length - 4] != 'x') {
                }
                if (toCharArray[length - 3] != 'p') {
                }
                if (toCharArray[length - 2] != 'o') {
                }
                if (toCharArray[length - 1] != 'r') {
                }
                if (toCharArray[length] != 't') {
                }
                i |= 3;
                i2 = length;
                // obj2 = null;
                while (i2 >= 6) {
                    switch (toCharArray[i2 - 6]) {
                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT192:
                    case IStaticDataEncryptComponent.GCRY_CIPHER_SERPENT256:
                    case OrderListBusiness.PAGE_SIZE:
                    case IStaticDataEncryptComponent.GCRY_CIPHER_CAMELLIA192:
                    case Bundle.ACTIVE:
                        break;
                    case Constants.LOGIN_HANDLER_KEY__APPCENTER:
                        obj = ACTION_EXPORT;
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "invalid permission: " + str);
                    }
                    i2--;
                }
                length = i2 - 6;
                // obj = obj2;
                // length = i3;
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
        if (!(permission instanceof PackagePermission)) {
            return false;
        }
        PackagePermission packagePermission = (PackagePermission) permission;
        return (this.action_mask & packagePermission.action_mask) == packagePermission.action_mask
                && super.implies(permission);
    }

    @Override
	public String getActions() {
        Object obj = ACTION_EXPORT;
        if (this.actions == null) {
            StringBuffer stringBuffer = new StringBuffer();
            if ((this.action_mask & 1) == 1) {
                stringBuffer.append(EXPORT);
            } else {
                obj = null;
            }
            if ((this.action_mask & 2) == 2) {
                if (obj != null) {
                    stringBuffer.append(',');
                }
                stringBuffer.append(IMPORT);
            }
            this.actions = stringBuffer.toString();
        }
        return this.actions;
    }

    @Override
	public PermissionCollection newPermissionCollection() {
        return new PackagePermissionCollection();
    }

    @Override
	public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof PackagePermission)) {
            return false;
        }
        PackagePermission packagePermission = (PackagePermission) obj;
        return this.action_mask == packagePermission.action_mask
                && getName().equals(packagePermission.getName());
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
