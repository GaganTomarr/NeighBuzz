export const validateUsername = (name: string) => {
    if (!name) return "Username is required.";
    if (/\s/.test(name)) return "Username should not contain spaces.";
    return null;
};

export const validateEmail = (mail: string) => {
    if (!mail) return "Email is required.";
    // Simple email regex
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(mail)) return "Please enter a valid email address.";
    return null;
};

export const validatePassword = (pwd: string) => {
    if (!pwd) return "Password is required.";
    if (pwd.length < 8)
        return "Password must be at least 8 characters long.";
    if (!/[A-Z]/.test(pwd))
        return "Password must contain at least one uppercase letter.";
    if (!/[a-z]/.test(pwd))
        return "Password must contain at least one lowercase letter.";
    if (!/[0-9]/.test(pwd))
        return "Password must contain at least one digit.";
    if (!/[!@#$%^&*(),.?":{}|<>]/.test(pwd))
        return "Password must contain at least one special character.";
    return null;
};

export const validateConfirmPassword = (pwd: string, confirmPwd: string) => {
    if (pwd !== confirmPwd) return "Passwords don't match.";
    return null;
};