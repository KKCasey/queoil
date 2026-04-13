package com.queoil.Queoil.controller;

import com.queoil.Queoil.model.Favourite;
import com.queoil.Queoil.model.Setlist;
import com.queoil.Queoil.model.Song;
import com.queoil.Queoil.model.User;
import com.queoil.Queoil.repository.FavouriteRepository;
import com.queoil.Queoil.repository.SetlistRepository;
import com.queoil.Queoil.repository.SongRepository;
import com.queoil.Queoil.repository.UserRepository;
import com.queoil.Queoil.model.SongRequest;
import com.queoil.Queoil.repository.SongRequestRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SetlistRepository setlistRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private FavouriteRepository favouriteRepository;

    @Autowired
    private SongRequestRepository songRequestRepository;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String email,
                               @RequestParam String password,
                               @RequestParam String role,
                               RedirectAttributes redirectAttributes) {

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);

        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage", "Registration successful!");

        return "redirect:/";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String loginUser(@RequestParam String email,
                            @RequestParam String password,
                            HttpSession session,
                            Model model) {

        Optional<User> userOptional = userRepository.findByEmailAndPassword(email, password);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            session.setAttribute("loggedInUser", user);

            if ("MUSICIAN".equals(user.getRole())) {
                return "redirect:/musician-dashboard";
            } else {
                return "redirect:/listener-dashboard";
            }
        }

        model.addAttribute("errorMessage", "Invalid email or password");
        return "login";
    }

    @PostMapping("/profile/update-password")
    public String updatePassword(@RequestParam String password,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        long userId = sessionUser.getId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        user.setPassword(password);
        userRepository.save(user);

        session.setAttribute("loggedInUser", user);
        redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully!");

        return "redirect:/profile";
    }

    @PostMapping("/profile/delete")
    public String deleteAccount(HttpSession session) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        long userId = sessionUser.getId();
        userRepository.deleteById(userId);
        session.invalidate();

        return "redirect:/";
    }

    @PostMapping("/setlists/create-ui")
    public String createSetlistFromUI(@RequestParam String title,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (!"MUSICIAN".equals(sessionUser.getRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only musicians can create setlists.");
            return "redirect:/listener-dashboard";
        }

        long userId = sessionUser.getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Setlist setlist = new Setlist();
        setlist.setTitle(title);
        setlist.setUser(user);

        setlistRepository.save(setlist);

        redirectAttributes.addFlashAttribute("successMessage", "Setlist created successfully!");

        return "redirect:/musician-dashboard";
    }

    @PostMapping("/songs/create-ui")
    public String createSongFromUI(@RequestParam String title,
                                @RequestParam long setlistId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (!"MUSICIAN".equals(sessionUser.getRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only musicians can add songs.");
            return "redirect:/listener-dashboard";
        }

        Setlist setlist = setlistRepository.findById(setlistId)
                .orElseThrow(() -> new RuntimeException("Setlist not found"));

        Song song = new Song();
        song.setTitle(title);
        song.setSetlist(setlist);

        songRepository.save(song);

        redirectAttributes.addFlashAttribute("successMessage", "Song added successfully!");

        return "redirect:/musician-dashboard";
    }

    @PostMapping("/musicians/{id}/follow")
    public String followMusician(@PathVariable long id,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User listener = (User) session.getAttribute("loggedInUser");
        if (listener == null) return "redirect:/login";

        if (!"LISTENER".equals(listener.getRole())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Only listeners can follow musicians.");
            return "redirect:/musicians";
        }

        if (favouriteRepository.findByListenerIdAndMusicianId(listener.getId(), id).isEmpty()) {

            User musician = userRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            Favourite fav = new Favourite();
            fav.setListener(listener);
            fav.setMusician(musician);

            favouriteRepository.save(fav);
        }

        return "redirect:/musicians/" + id;
    }

    @PostMapping("/musicians/{id}/unfollow")
    public String unfollowMusician(@PathVariable long id,
                                HttpSession session) {

        User listener = (User) session.getAttribute("loggedInUser");
        if (listener == null) return "redirect:/login";

        favouriteRepository.findByListenerIdAndMusicianId(listener.getId(), id)
                .ifPresent(favouriteRepository::delete);

        return "redirect:/musicians/" + id;
    }

    @PostMapping("/live/start")
    public String startLive(@RequestParam long setlistId,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {

        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        long userId = sessionUser.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Setlist setlist = setlistRepository.findById(setlistId)
                .orElseThrow(() -> new RuntimeException("Setlist not found"));

        user.setLiveNow(true);
        user.setActiveSetlist(setlist);
        userRepository.save(user);

        session.setAttribute("loggedInUser", user);
        redirectAttributes.addFlashAttribute("successMessage", "You are now live!");

        return "redirect:/musician-dashboard";
    }

    @PostMapping("/live/end")
    public String endLive(HttpSession session, RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        long userId = sessionUser.getId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLiveNow(false);
        user.setActiveSetlist(null);
        userRepository.save(user);

        session.setAttribute("loggedInUser", user);
        redirectAttributes.addFlashAttribute("successMessage", "Show ended.");

        return "redirect:/musician-dashboard";
    }

    @PostMapping("/requests/create")
    public String createRequest(@RequestParam long songId,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        User listener = (User) session.getAttribute("loggedInUser");
        if (listener == null) {
            return "redirect:/login";
        }

        if (!"LISTENER".equals(listener.getRole())) {
            return "redirect:/listener-dashboard";
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("Song not found"));

        Setlist setlist = song.getSetlist();
        User musician = setlist.getUser();

        if (musician == null || !musician.isLiveNow() || musician.getActiveSetlist() == null
                || !musician.getActiveSetlist().getId().equals(setlist.getId())) {
            redirectAttributes.addFlashAttribute("errorMessage", "Requests are only available for active live setlists.");
            return "redirect:/setlists/" + setlist.getId();
        }

        SongRequest request = new SongRequest();
        request.setSong(song);
        request.setListener(listener);
        request.setMusician(musician);
        request.setStatus("PENDING");
        request.setCreatedAt(java.time.LocalDateTime.now());

        songRequestRepository.save(request);

        redirectAttributes.addFlashAttribute("successMessage", "Song request submitted!");
        return "redirect:/setlists/" + setlist.getId();
    }

    @PostMapping("/requests/{id}/played")
    public String markPlayed(@PathVariable long id) {
        SongRequest request = songRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus("PLAYED");
        songRequestRepository.save(request);
        return "redirect:/requests";
    }

    @PostMapping("/requests/{id}/deny")
    public String markDenied(@PathVariable long id) {
        SongRequest request = songRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        request.setStatus("DENIED");
        songRequestRepository.save(request);
        return "redirect:/requests";
    }

    @GetMapping("/my-setlists")
    public String mySetlistsPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!"MUSICIAN".equals(user.getRole())) {
            return "redirect:/listener-dashboard";
        }

        long userId = user.getId();
        model.addAttribute("setlists", setlistRepository.findByUserId(userId));

        return "my-setlists";
    }

    @GetMapping("/requests")
    public String requestsPage(HttpSession session, Model model) {
        User musician = (User) session.getAttribute("loggedInUser");
        if (musician == null) {
            return "redirect:/login";
        }

        if (!"MUSICIAN".equals(musician.getRole())) {
            return "redirect:/listener-dashboard";
        }

        model.addAttribute("requests", songRequestRepository.findByMusicianIdAndStatusOrderByCreatedAtAsc(musician.getId(), "PENDING"));
        return "requests";
    }

    @GetMapping("/musician-dashboard")
    public String musicianDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "musician-dashboard";
    }

    @GetMapping("/listener-dashboard")
    public String listenerDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "listener-dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("/musicians")
    public String musiciansPage(@RequestParam(required = false) String search, Model model) {

        if (search != null && !search.isBlank()) {
            model.addAttribute("users", userRepository.findByRoleAndUsernameContainingIgnoreCase("MUSICIAN", search));
        } else {
            model.addAttribute("users", userRepository.findByRole("MUSICIAN"));
        }

        model.addAttribute("search", search);

        return "musicians";
    }

    @GetMapping("/musicians/{id}")
    public String musicianDetails(@PathVariable long id, HttpSession session, Model model) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        model.addAttribute("musician", user);
        model.addAttribute("setlists", setlistRepository.findByUserId(id));

        User loggedInUser = (User) session.getAttribute("loggedInUser");
        boolean isFollowing = false;

        if (loggedInUser != null && "LISTENER".equals(loggedInUser.getRole())) {
            isFollowing = favouriteRepository
                    .findByListenerIdAndMusicianId(loggedInUser.getId(), id)
                    .isPresent();
        }

        model.addAttribute("isFollowing", isFollowing);
        model.addAttribute("loggedInUser", loggedInUser);

        return "musician-details";
    }

    @GetMapping("/setlists/{id}")
    public String setlistDetails(@PathVariable long id, HttpSession session, Model model) {
        Setlist setlist = setlistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Setlist not found"));

        User loggedInUser = (User) session.getAttribute("loggedInUser");

        boolean requestAllowed = false;
        User musician = setlist.getUser();

        if (musician != null && musician.isLiveNow() && musician.getActiveSetlist() != null) {
            requestAllowed = musician.getActiveSetlist().getId().equals(setlist.getId());
        }

        model.addAttribute("setlist", setlist);
        model.addAttribute("songs", songRepository.findBySetlistId(id));
        model.addAttribute("loggedInUser", loggedInUser);
        model.addAttribute("requestAllowed", requestAllowed);

        return "setlist-details";
    }

    @GetMapping("/profile")
    public String profilePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "profile";
    }

    @GetMapping("/setlists/create-ui")
    public String createSetlistPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!"MUSICIAN".equals(user.getRole())) {
            model.addAttribute("errorMessage", "Only musicians can create setlists.");
            return "listener-dashboard";
        }

        return "create-setlist";
    }

    @GetMapping("/songs/create-ui")
    public String addSongPage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            return "redirect:/login";
        }

        if (!"MUSICIAN".equals(user.getRole())) {
            model.addAttribute("errorMessage", "Only musicians can add songs.");
            return "listener-dashboard";
        }

        long userId = user.getId();
        model.addAttribute("setlists", setlistRepository.findByUserId(userId));

        return "add-song";
    }

    @GetMapping("/favourites")
    public String favouritesPage(HttpSession session, Model model) {
        User listener = (User) session.getAttribute("loggedInUser");
        if (listener == null) {
            return "redirect:/login";
        }

        if (!"LISTENER".equals(listener.getRole())) {
            return "redirect:/musician-dashboard";
        }

        List<Favourite> favourites = favouriteRepository.findByListenerId(listener.getId());

        List<User> musicians = favourites.stream()
                .map(Favourite::getMusician)
                .toList();

        model.addAttribute("favourites", musicians);
        return "favourites";
    }

    @GetMapping("/live")
    public String livePage(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("loggedInUser");
        if (sessionUser == null) {
            return "redirect:/login";
        }

        if (!"MUSICIAN".equals(sessionUser.getRole())) {
            return "redirect:/listener-dashboard";
        }

        long userId = sessionUser.getId();
        model.addAttribute("setlists", setlistRepository.findByUserId(userId));

        return "go-live";
    }

}