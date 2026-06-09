import React, { useState, useEffect } from "react";
import { Header } from "./components/Header";
import Filtering from "./components/Filtering";
import FoodCard from "./components/FoodCard";
import Posting from "./components/Posting";
import Reservations, { ReserveDialog } from "./components/Reservation";
import Communities from "./components/Communities";
import Accessibility from "./components/Accessibility";
import HomePage from "./components/HomePage";
import AuthPage from "./components/LoginPage";
import {
  getFoodItems,
  createReservation,
  getMyReservations
} from "./api/FoodPostingApi";
import { getCommunities } from "./api/CommunityApi";

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [currentUser, setCurrentUser] = useState("");
  const [currentUserId, setCurrentUserId] = useState(null);

  const [currentView, setCurrentView] = useState("home");
  const [foodItems, setFoodItems] = useState([]);
  const [reservations, setReservations] = useState([]);
  const [reserveTarget, setReserveTarget] = useState(null);

  const [searchQuery, setSearchQuery] = useState("");
  const [selectedAllergens, setSelectedAllergens] = useState([]);
  const [expiryFilter, setExpiryFilter] = useState("all");

  const [prefilledCommunity, setPrefilledCommunity] = useState("");
  const [communities, setCommunities] = useState([]);

  const [joinedCommunities, setJoinedCommunities] = useState(() => {
    const saved = localStorage.getItem("joinedCommunities");
    return saved ? JSON.parse(saved) : [];
  });

  const [a11y, setA11y] = useState({
    darkMode: false,
    largeText: false,
    highContrast: false,
    ariaLabels: true,
  });

  useEffect(() => {
    localStorage.setItem("joinedCommunities", JSON.stringify(joinedCommunities));
  }, [joinedCommunities]);

  useEffect(() => {
    const loadFoodItems = async () => {
      try {
        const data = await getFoodItems();
        const items = Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : [];

        const mappedItems = items.map((item) => ({
          ...item,
          imageUrl: item.imageUrl || item.image,
          expiryDate: item.expiryDate || item.expiry,
        }));

        setFoodItems(mappedItems);
      } catch (error) {
        console.error("Failed to load food items:", error);
        setFoodItems([]);
      }
    };

    loadFoodItems();
  }, []);

  useEffect(() => {
    const loadCommunities = async () => {
      try {
        const data = await getCommunities();
        const list = Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : [];

        setCommunities(list);
      } catch (error) {
        console.error("Failed to load communities:", error);
        setCommunities([]);
      }
    };

    loadCommunities();
  }, []);

  const handleLogin = (user) => {
    setCurrentUser(user.username);
    setCurrentUserId(user.id);
    setIsLoggedIn(true);
  };

  const handleLogout = () => {
    setIsLoggedIn(false);
    setCurrentUser("");
    setCurrentUserId(null);
    setCurrentView("home");
    setReservations([]);
    setJoinedCommunities([]);
    localStorage.removeItem("joinedCommunities");
  };

  useEffect(() => {
    if (!isLoggedIn || !currentUserId) return;

    const token = localStorage.getItem("token");
    if (!token) return;

    getMyReservations(token)
      .then((data) => {
        const reservationList = Array.isArray(data)
          ? data
          : Array.isArray(data?.content)
          ? data.content
          : [];

        setReservations(reservationList);

        const reservedIds = reservationList.map((r) => r.foodItemId);
        setFoodItems((prev) =>
          prev.map((f) => ({
            ...f,
            reserved: reservedIds.includes(f.id),
          }))
        );
      })
      .catch((err) => console.error(err));
  }, [isLoggedIn, currentUserId]);

  const handleFoodPosted = (foodData) => {
    setFoodItems((prev) => [
      ...prev,
      {
        ...foodData,
        id: Date.now(),
        reserved: false,
        postedByMe: true,
        donor: currentUser,
      },
    ]);
  };

  const handleReserveConfirm = (item) => {
    const token = localStorage.getItem("token");
    if (!token || !currentUserId) return;

    createReservation(item.id, currentUserId, 1, token)
      .then((reservation) => {
        setFoodItems((prev) =>
          prev.map((f) =>
            f.id === reservation.foodItemId ? { ...f, reserved: true } : f
          )
        );
        setReservations((prev) => [...prev, reservation]);
      })
      .catch((err) => console.error(err));
  };

  const handleCancelReservation = (reservationId) => {
    const cancelledReservation = reservations.find((r) => r.id === reservationId);
    setReservations((prev) => prev.filter((r) => r.id !== reservationId));

    if (cancelledReservation) {
      setFoodItems((prev) =>
        prev.map((f) =>
          f.id === cancelledReservation.foodItemId
            ? { ...f, reserved: false }
            : f
        )
      );
    }
  };

  const handleAllergenToggle = (allergen) => {
    setSelectedAllergens((prev) =>
      prev.includes(allergen)
        ? prev.filter((a) => a !== allergen)
        : [...prev, allergen]
    );
  };

  const handleClearFilters = () => {
    setSearchQuery("");
    setSelectedAllergens([]);
    setExpiryFilter("all");
  };

  const getFilteredItems = () => {
    const today = new Date();
    today.setHours(0, 0, 0, 0);

    return foodItems.filter((item) => {
      if (
        searchQuery &&
        !item.name.toLowerCase().includes(searchQuery.toLowerCase()) &&
        !item.description.toLowerCase().includes(searchQuery.toLowerCase())
      ) {
        return false;
      }

      if (
        selectedAllergens.length > 0 &&
        item.allergens?.some((a) => selectedAllergens.includes(a))
      ) {
        return false;
      }

      const expiry = new Date(item.expiryDate);
      expiry.setHours(0, 0, 0, 0);
      const daysUntilExpiry = Math.ceil(
        (expiry - today) / (1000 * 60 * 60 * 24)
      );

      if (expiryFilter === "today" && daysUntilExpiry !== 0) return false;
      if (expiryFilter === "3days" && daysUntilExpiry > 3) return false;
      if (expiryFilter === "week" && daysUntilExpiry > 7) return false;

      return true;
    });
  };

  const filteredItems = getFilteredItems();

  const myJoinedCommunities = communities.filter((c) =>
    joinedCommunities.map(Number).includes(Number(c.id))
  );

  const communityFoodItems = foodItems.filter((item) =>
    myJoinedCommunities.some((c) => c.name === item.community)
  );

  if (!isLoggedIn) {
    return <AuthPage onLogin={handleLogin} />;
  }

  return (
    <div
      className={[
        a11y.darkMode ? "a11y-dark" : "",
        a11y.largeText ? "a11y-large-text" : "",
        a11y.highContrast ? "a11y-high-contrast" : "",
      ]
        .filter(Boolean)
        .join(" ")}
      style={{
        minHeight: "100vh",
        backgroundColor: a11y.darkMode ? "#121212" : "#fff",
      }}
    >
      <Header
        currentView={currentView}
        onViewChange={setCurrentView}
        a11y={a11y}
        currentUser={currentUser}
        onLogout={handleLogout}
      />

      {currentView === "home" && (
        <HomePage
          foodItems={foodItems}
          joinedCommunities={myJoinedCommunities}
          communityFoodItems={communityFoodItems}
          onReserve={setReserveTarget}
          onViewChange={setCurrentView}
          a11y={a11y}
          currentUser={currentUser}
        />
      )}

      {currentView === "browse" && (
        <>
          <Filtering
            searchQuery={searchQuery}
            onSearchChange={setSearchQuery}
            selectedAllergens={selectedAllergens}
            onAllergenToggle={handleAllergenToggle}
            expiryFilter={expiryFilter}
            onExpiryFilterChange={setExpiryFilter}
            onClearFilters={handleClearFilters}
          />
          <div
            style={{
              maxWidth: "1280px",
              margin: "0 auto",
              padding: "2rem 1rem",
              display: "grid",
              gridTemplateColumns: "repeat(auto-fill, minmax(300px, 1fr))",
              gap: "20px",
            }}
          >
            {filteredItems.map((item) => (
              <FoodCard
                key={item.id}
                item={item}
                onReserve={setReserveTarget}
              />
            ))}
          </div>
        </>
      )}

      {currentView === "post" && (
        <Posting
          onFoodPosted={handleFoodPosted}
          onViewChange={setCurrentView}
          prefilledCommunity={prefilledCommunity}
          onUnmount={() => setPrefilledCommunity("")}
        />
      )}

      {currentView === "reservations" && (
        <Reservations
          reservations={reservations}
          onCancelReservation={handleCancelReservation}
          a11y={a11y}
        />
      )}

      {currentView === "communities" && (
        <Communities
          foodItems={foodItems}
          onReserve={setReserveTarget}
          a11y={a11y}
          joinedCommunities={joinedCommunities}
          onJoinedCommunitiesChange={setJoinedCommunities}
          currentUserProfileId={currentUserId}
          onPostFood={(communityName) => {
            setPrefilledCommunity(communityName);
            setCurrentView("post");
          }}
        />
      )}

      {currentView === "settings" && (
        <Accessibility a11y={a11y} onA11yChange={setA11y} />
      )}

      <ReserveDialog
        open={!!reserveTarget}
        item={reserveTarget}
        onClose={() => setReserveTarget(null)}
        onConfirm={handleReserveConfirm}
      />
    </div>
  );
}

export default App;