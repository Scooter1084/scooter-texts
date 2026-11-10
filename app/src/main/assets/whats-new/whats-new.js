/*
 * Scooter "What's new" — shows the shared card (whats-new.css) once per new note, in any app.
 *
 *   ScooterWhatsNew.show({
 *     app: "Scooter Fit",                     // the line under the headline: "Scooter Fit · this update"
 *     storageKey: "scooter-fit-whats-new",    // where this phone remembers the notes it has shown
 *     notes: [                                // newest first; each note shows once, ever
 *       { id: "2026-10-10-water", icon: "💧", text: "**Water is shared.** Every glass from every app shows here." },
 *     ],
 *   });
 *
 * Options (all but notes optional):
 *   app          the app's name, for the line under the headline
 *   storageKey   localStorage key for the shown notes (default "whats-new-seen")
 *   existingUser true when the app already has this person's data: on the first run with the kit, their newest
 *                notes show. false/absent = a fresh install: everything is marked seen and nothing shows.
 *   all          true = show every note, newest first, grouped "Latest update" / "Earlier" (a Settings link)
 *   maxNotes     at most this many notes on one card (default 8)
 *   button       the button's words (default "Let's Gooooo!!")
 *   brand        a color for the headline and button (default Scooter orange)
 *   light        true = always the light card, even in dark mode
 *   onClose      called when it closes (button, Escape/Back, or a tap outside)
 * Returns a Promise: true if the card was shown, false if there was nothing new.
 *
 * Note text is plain words; **like this** makes bold. Notes keep their "id" forever (rewording keeps the id,
 * so it doesn't show again); without an id, the words themselves are the id.
 */
(function (global) {
  "use strict";

  function markOf(note) {
    if (note.id) return "id:" + note.id;
    var text = String(note.text || ""), hash = 0;
    for (var i = 0; i < text.length; i++) hash = (hash * 31 + text.charCodeAt(i)) | 0;
    return "t:" + (hash >>> 0).toString(36);
  }

  function readSeen(key) {
    try { var list = JSON.parse(localStorage.getItem(key) || "null"); return Array.isArray(list) ? list : null; }
    catch (e) { return null; }
  }

  function writeSeen(key, notes) {
    writeMarks(key, notes.map(markOf));
  }

  function writeMarks(key, marks) {
    try { localStorage.setItem(key, JSON.stringify(marks)); } catch (e) { /* storage off: shows again next time */ }
  }

  /** Plain words with **bold**, safely (no HTML from the note itself). */
  function fill(element, text) {
    String(text || "").split("**").forEach(function (part, i) {
      if (!part) return;
      if (i % 2 === 1) { var b = document.createElement("b"); b.textContent = part; element.appendChild(b); }
      else element.appendChild(document.createTextNode(part));
    });
  }

  function show(options) {
    var o = options || {};
    var notes = (o.notes || []).filter(function (n) { return n && n.text; });
    var key = o.storageKey || "whats-new-seen";
    var seen = readSeen(key);
    var fresh;
    if (o.all) fresh = notes;
    else if (seen === null && !o.existingUser) { writeSeen(key, notes); return Promise.resolve(false); }
    else fresh = notes.filter(function (n) { return !seen || seen.indexOf(markOf(n)) < 0; });
    fresh = fresh.slice(0, o.all ? notes.length : (o.maxNotes || 8));
    if (!fresh.length) { writeSeen(key, notes); return Promise.resolve(false); }

    return new Promise(function (resolve) {
      var backdrop = document.createElement("div");
      backdrop.className = "wn-backdrop" + (o.light ? " wn-light" : "");
      if (o.brand) backdrop.style.setProperty("--wn-brand", o.brand);
      var card = document.createElement("div");
      card.className = "wn-card";
      card.setAttribute("role", "dialog");
      card.setAttribute("aria-modal", "true");
      card.setAttribute("aria-labelledby", "wn-title");
      var burst = document.createElement("div");
      burst.className = "wn-burst";
      burst.id = "wn-title";
      burst.textContent = "What's new!";
      var sub = document.createElement("div");
      sub.className = "wn-sub";
      sub.textContent = (o.app ? o.app + " · " : "") + (o.all ? "every update so far" : "this update");
      var list = document.createElement("ul");
      list.className = "wn-list";
      fresh.forEach(function (note, i) {
        // One "Latest update" heading, then one "Earlier" where the older updates start.
        if (o.all && note.update !== undefined && (i === 0 || (fresh[i - 1].update === fresh[0].update && note.update !== fresh[0].update))) {
          var group = document.createElement("li");
          group.className = "wn-group";
          group.textContent = i === 0 ? "Latest update" : "Earlier";
          list.appendChild(group);
        }
        var li = document.createElement("li");
        var ic = document.createElement("span");
        ic.className = "wn-ic";
        ic.setAttribute("aria-hidden", "true");
        ic.textContent = note.icon || "✨";
        var words = document.createElement("span");
        fill(words, note.text);
        li.appendChild(ic);
        li.appendChild(words);
        list.appendChild(li);
      });
      var go = document.createElement("button");
      go.type = "button";
      go.className = "wn-go";
      go.textContent = o.button || "Let's Gooooo!!";
      card.appendChild(burst);
      card.appendChild(sub);
      card.appendChild(list);
      card.appendChild(go);
      backdrop.appendChild(card);

      var closed = false;
      function close() {
        if (closed) return;
        closed = true;
        document.removeEventListener("keydown", onKey);
        if (backdrop.parentNode) backdrop.parentNode.removeChild(backdrop);
        if (typeof o.onClose === "function") o.onClose();
        resolve(true);
      }
      function onKey(event) { if (event.key === "Escape") close(); }
      go.addEventListener("click", close);
      backdrop.addEventListener("click", function (event) { if (event.target === backdrop) close(); });
      document.addEventListener("keydown", onKey);

      document.body.appendChild(backdrop);
      // Shown counts as seen, however it's closed. Notes that didn't fit (more than maxNotes) show next time; on the
      // first run with the kit, everything older counts as seen.
      if (o.all || seen === null) writeSeen(key, notes);
      else writeMarks(key, seen.concat(fresh.map(markOf)));
      go.focus();
    });
  }

  var api = { show: show };
  global.ScooterWhatsNew = api;
  if (typeof module === "object" && module.exports) module.exports = api;
})(typeof window !== "undefined" ? window : this);
