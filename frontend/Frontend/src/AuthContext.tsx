// import React, {
//   createContext,
//   useState,
//   useContext,
//   useEffect,
//   ReactNode,
// } from "react";

// import { useNavigate } from "react-router-dom";

// interface AuthContextType {
//   token: string | null;

//   login: (jwtToken: string, username?: string, userId?: number) => void;

//   logout: () => void;

//   isAuthenticated: boolean;

//   username: string | null;

//   userId: number | null;
// }

// const AuthContext = createContext<AuthContextType | undefined>(undefined);

// export const AuthProvider: React.FC<{ children: ReactNode }> = ({
//   children,
// }) => {
//   const [token, setToken] = useState<string | null>(
//     localStorage.getItem("authToken")
//   );

//   const [username, setUsername] = useState<string | null>(
//     localStorage.getItem("username")
//   );

//   const [userId, setUserId] = useState<number | null>(() => {
//     const storedUserId = localStorage.getItem("userId");

//     return storedUserId ? parseInt(storedUserId) : null;
//   });

//   const navigate = useNavigate();

//   useEffect(() => {
//     const storedToken = localStorage.getItem("authToken");

//     const storedUsername = localStorage.getItem("username");

//     const storedUserId = localStorage.getItem("userId");

//     if (storedToken) {
//       setToken(storedToken);

//       if (storedUsername) {
//         setUsername(storedUsername);
//       }

//       if (storedUserId) {
//         setUserId(parseInt(storedUserId));
//       }
//     }
//   }, []);

//   const login = (jwtToken: string, username?: string, userId?: number) => {
//     localStorage.setItem("authToken", jwtToken);

//     setToken(jwtToken);

//     if (username) {
//       localStorage.setItem("username", username);

//       setUsername(username);
//     }

//     if (userId !== undefined) {
//       localStorage.setItem("userId", userId.toString());

//       setUserId(userId);
//     }

//     console.log("UserId set:", userId);

//     console.log("Username set:", username);
//   };

//   const logout = () => {
//     localStorage.removeItem("authToken");

//     localStorage.removeItem("username");

//     localStorage.removeItem("userId");

//     const currentPath = window.location.pathname;

//     const isProtected = ["/events/create", "/news/add", "/profile"].some(
//       (route) => currentPath.startsWith(route)
//     );

//     if (isProtected) {
//       if (currentPath.startsWith("/events/create")) {
//         navigate("/events");
//       } else if (currentPath.startsWith("/news/add")) {
//         navigate("/news");
//       } else if (currentPath.startsWith("/profile")) {
//         navigate("/");
//       } else {
//         navigate("/");
//       }
//     }

//     setToken(null);

//     setUsername(null);

//     setUserId(null);
//   };

//   const isAuthenticated = !!token;

//   return (
//     <AuthContext.Provider
//       value={{ token, login, logout, isAuthenticated, username, userId }}
//     >
//       {children}
//     </AuthContext.Provider>
//   );
// };

// export const useAuth = (): AuthContextType => {
//   const context = useContext(AuthContext);

//   if (context === undefined) {
//     throw new Error("useAuth must be used within an AuthProvider");
//   }

//   return context;
// };


import React, {
  createContext,
  useState,
  useContext,
  useEffect,
  ReactNode,
} from "react";

import { useNavigate } from "react-router-dom";

interface AuthContextType {
  token: string | null;
  login: (
    jwtToken: string,
    username?: string,
    userId?: number,
    isAdmin?: boolean
  ) => void;
  logout: () => void;
  isAuthenticated: boolean;
  username: string | null;
  userId: number | null;
  isAdmin: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: ReactNode }> = ({
  children,
}) => {
  const [token, setToken] = useState<string | null>(
    localStorage.getItem("authToken")
  );

  const [username, setUsername] = useState<string | null>(
    localStorage.getItem("username")
  );

  const [userId, setUserId] = useState<number | null>(() => {
    const storedUserId = localStorage.getItem("userId");
    return storedUserId ? parseInt(storedUserId) : null;
  });

  const [isAdmin, setIsAdmin] = useState<boolean>(() => {
    const storedIsAdmin = localStorage.getItem("isAdmin");
    return storedIsAdmin === "true";
  });

  const navigate = useNavigate();

  useEffect(() => {
    const storedToken = localStorage.getItem("authToken");
    const storedUsername = localStorage.getItem("username");
    const storedUserId = localStorage.getItem("userId");
    const storedIsAdmin = localStorage.getItem("isAdmin");

    if (storedToken) {
      setToken(storedToken);

      if (storedUsername) {
        setUsername(storedUsername);
      }

      if (storedUserId) {
        setUserId(parseInt(storedUserId));
      }

      if (storedIsAdmin) {
        setIsAdmin(storedIsAdmin === "true");
      }
    }
  }, []);

  const login = (
    jwtToken: string,
    username?: string,
    userId?: number,
    isAdminParam?: boolean
  ) => {
    localStorage.setItem("authToken", jwtToken);
    setToken(jwtToken);

    if (username) {
      localStorage.setItem("username", username);
      setUsername(username);
    }

    if (userId !== undefined) {
      localStorage.setItem("userId", userId.toString());
      setUserId(userId);
    }

    if (isAdminParam !== undefined) {
      localStorage.setItem("isAdmin", isAdminParam.toString());
      setIsAdmin(isAdminParam);
    }

    console.log("UserId set:", userId);
    console.log("Username set:", username);
    console.log("isAdmin set:", isAdminParam);
  };

  const logout = () => {
    localStorage.removeItem("authToken");
    localStorage.removeItem("username");
    localStorage.removeItem("userId");
    localStorage.removeItem("isAdmin");

    const currentPath = window.location.pathname;

    const isProtected = ["/events/create", "/news/add", "/profile"].some(
      (route) => currentPath.startsWith(route)
    );

    if (isProtected) {
      if (currentPath.startsWith("/events/create")) {
        navigate("/events");
      } else if (currentPath.startsWith("/news/add")) {
        navigate("/news");
      } else if (currentPath.startsWith("/profile")) {
        navigate("/");
      } else {
        navigate("/");
      }
    }

    setToken(null);
    setUsername(null);
    setUserId(null);
    setIsAdmin(false);
  };

  const isAuthenticated = !!token;

  return (
    <AuthContext.Provider
      value={{ token, login, logout, isAuthenticated, username, userId, isAdmin }}
    >
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = useContext(AuthContext);

  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }

  return context;
};