import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

class Candidate {
    String name;
    int totalPoints;

    Candidate(String name) {
        this.name = name;
        this.totalPoints = 0;
    }
}

class Region {
    String name;
    String contestants;
    Map<String, Candidate> candidates;
    Map<String, Integer> invalidVotes;

    Region(String name, String contestants) {
        this.name = name;
        this.contestants = contestants;
        this.candidates = new HashMap<>();
        this.invalidVotes = new HashMap<>();

        for (char c : contestants.toCharArray()) {
            this.candidates.put(String.valueOf(c), new Candidate(String.valueOf(c)));
        }
    }

    void countVotes(String regionName,List<String> votes) {
        for (String vote : votes) {
            String[] parts = vote.split(" ");
            String voterRegion = regionName;
            String voterId = parts[0];
            String preferences = parts[1];

            if (!voterRegion.equals(this.name)) {
                this.invalidVotes.put(voterId, this.invalidVotes.getOrDefault(voterId, 0) + 1);
                continue;
            }

            if (preferences.length() == 0 || preferences.length() > 3) {
                this.invalidVotes.put(voterId, this.invalidVotes.getOrDefault(voterId, 0) + 1);
                continue;
            }

            for (int i = 0; i < preferences.length(); i++) {
                String candidateName = String.valueOf(preferences.charAt(i));
                if (!this.contestants.contains(candidateName)) {
                    this.invalidVotes.put(voterId, this.invalidVotes.getOrDefault(voterId, 0) + 1);
                    break;
                }
                Candidate candidate = this.candidates.get(candidateName);
                candidate.totalPoints += (3 - i);
            }
        }
    }

    Candidate getRegionalHead() {
        Candidate regionalHead = null;
        int maxPoints = Integer.MIN_VALUE;

        for (Candidate candidate : this.candidates.values()) {
            if (candidate.totalPoints > maxPoints) {
                maxPoints = candidate.totalPoints;
                regionalHead = candidate;
            }
        }

        return regionalHead;
    }
}

public class ElectionSystem {

    public static void main(String[] args) {
        File file = new File("C:\\Users\\abhil\\IdeaProjects\\Interview1\\src\\input.dat");
        List<Region> regions = new ArrayList<>();

        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.equals("&&")) break;
                if (line.startsWith("//")) continue;

                String[] parts = line.split("/");


                String regionName = parts[0];
                System.out.println(regionName);
                String contestants = parts[1];
                System.out.println(contestants);

                Region region = new Region(regionName, contestants);
                regions.add(region);

                List<String> votes = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    String nextLine = scanner.nextLine();
                    if (nextLine.equals("//")) break;
                    votes.add(nextLine);

                }
                region.countVotes(regionName,votes);
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Calculate Chief Officer
        Candidate chiefOfficer = null;
        int maxPoints = Integer.MIN_VALUE;
        for (Region region : regions) {
            for (Candidate candidate : region.candidates.values()) {
                if (candidate.totalPoints > maxPoints) {
                    maxPoints = candidate.totalPoints;
                    chiefOfficer = candidate;
                }
            }
        }

        // Print Chief Officer
        System.out.println("CHIEF OFFICER: " + chiefOfficer.name + " with " + maxPoints + " points.");

        // Print Regional Heads and Invalid Votes
        for (Region region : regions) {
            Candidate regionalHead = region.getRegionalHead();
            int invalidVotes = region.invalidVotes.size();
            System.out.println("REGION: " + region.name);
            System.out.println("Invalid Votes: " + invalidVotes);
            System.out.println("REGIONAL HEAD: " + regionalHead.name + " with " + regionalHead.totalPoints + " points.");
        }
    }
}
