import React, { useState } from "react";

import {
  Card,
  CardContent,
  CardActions,
  Collapse,
  Typography,
  IconButton,
  Grid,
} from "@mui/material";

import ExpandMoreIcon from "@mui/icons-material/ExpandMore";

import LocationOnIcon from "@mui/icons-material/LocationOn";

import CalendarTodayIcon from "@mui/icons-material/CalendarToday";

import "../styles/EventCard.css";

interface EventCardProps {
  id: string;

  title: string;

  description: string;

  capacity: string;

  startDate: string;

  endDate: string;

  location: string;
}

const EventCard: React.FC<EventCardProps> = ({
  id,

  title,

  description,

  capacity,

  startDate,

  endDate,

  location,
}) => {
  const [expanded, setExpanded] = useState(false);

  const handleExpandClick = () => {
    setExpanded(!expanded);
  };

  return (
    <Card className="event-card">
      <CardContent className="event-card-content">
        <Typography variant="h5" component="div" className="event-title">
          {title}
        </Typography>

        <div className="event-basic-info">
          <div className="event-info-item">
            <CalendarTodayIcon fontSize="small" />

            <Typography variant="body2" color="text.secondary">
              {new Date(startDate).toLocaleDateString()}
            </Typography>
          </div>

          <div className="event-info-item">
            <LocationOnIcon fontSize="small" />

            <Typography variant="body2" color="text.secondary">
              {location}
            </Typography>
          </div>
        </div>
      </CardContent>

      <CardActions disableSpacing className="event-card-actions">
        <IconButton
          className={`expand-icon ${expanded ? "expanded" : ""}`}
          onClick={handleExpandClick}
          aria-expanded={expanded}
          aria-label="show more"
        >
          <ExpandMoreIcon />
        </IconButton>
      </CardActions>

      {/* <Collapse in={expanded} timeout="auto" unmountOnExit>
        <CardContent className="event-card-details">
          <Grid container spacing={2}>
            <Grid item xs={12}>
              <Typography variant="h6">Description</Typography>

              <Typography component="p">{description}</Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography variant="h6">Capacity</Typography>

              <Typography component="p">{capacity}</Typography>
            </Grid>

            <Grid item xs={6}>
              <Typography variant="h6">Dates</Typography>

              <Typography component="p">
                From: {new Date(startDate).toLocaleDateString()}
                <br />
                To: {new Date(endDate).toLocaleDateString()}
              </Typography>
            </Grid>

            <Grid item xs={12}>
              <Typography variant="h6">Location</Typography>

              <Typography component="p">{location}</Typography>
            </Grid>

            <Grid item xs={12} className="event-card-buttons">
              <button className="register-btn">Register</button>

              <button className="details-btn">View Details</button>
            </Grid>
          </Grid>
        </CardContent>
      </Collapse> */}
    </Card>
  );
};

export default EventCard;
